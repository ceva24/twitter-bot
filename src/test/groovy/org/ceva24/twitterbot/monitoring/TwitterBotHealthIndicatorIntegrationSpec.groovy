package org.ceva24.twitterbot.monitoring

import org.apache.tomcat.util.codec.binary.Base64
import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.ApplicationStatus
import org.ceva24.twitterbot.domain.Tweet
import org.ceva24.twitterbot.repository.ApplicationStatusRepository
import org.ceva24.twitterbot.repository.TweetRepository
import org.ceva24.twitterbot.TwitterBot
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeZone
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Status
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@ActiveProfiles('test')
@WebIntegrationTest(randomPort = true)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotHealthIndicatorIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    int port

    @Value('${security.user.name}')
    String username

    @Value('${security.user.password}')
    String password

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Integer downtimePeriod

    @Autowired
    TweetRepository tweetRepository

    @Autowired
    ApplicationStatusRepository applicationStatusRepository

    @Autowired
    TwitterBot twitterBot

    RestTemplate restTemplate
    HttpHeaders authenticationHeader

    def setup() {

        restTemplate = new TestRestTemplate()

        authenticationHeader = new HttpHeaders()
        authenticationHeader.add 'Authorization', "Basic ${new String(Base64.encodeBase64("${username}:${password}".bytes))}"

        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME)
    }

    def cleanup() {

        tweetRepository.deleteAll()
        applicationStatusRepository.deleteAll()
    }

    def 'unauthenticated requests returns http unauthorized'() {

        when:
        def response = restTemplate.getForEntity "http://localhost:${port}/health", Map

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED

        and:
        response.body.status == HttpStatus.UNAUTHORIZED.value()
    }

    def 'the health indicator shows the last tweet as null when no tweets have been sent'() {

        setup:
        tweetRepository.save new Tweet(id: 1, text: 'test 1', sequenceNo: 1)

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        !response.body.twitterBot.lastTweet.id
    }

    def 'the health indicator shows the last tweet that was sent'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        tweetRepository.save([new Tweet(id: 1, text: 'test 1', sequenceNo: 1), new Tweet(id: 2, text: 'test 2', sequenceNo: 2)])

        when:
        twitterBot.tweet()

        and:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.twitterBot.lastTweet.tweetedOn == new DateTime(100000, DateTimeZone.UTC).toString()
        response.body.twitterBot.lastTweet.id == 1
        response.body.twitterBot.lastTweet.text == 'test 1'
    }

    def 'the health indicator shows when the downtime period is inactive'() {

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        !response.body.twitterBot.isDowntimePeriodActive
        response.body.twitterBot.downtimePeriodRemaining == 0
    }

    def 'the  health indicator shows when the downtime period is active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME, activeOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.twitterBot.isDowntimePeriodActive
        response.body.twitterBot.downtimePeriodRemaining == downtimePeriod
    }

    def 'when the service is tweeting as normal the health indicator shows up'() {

        setup:
        tweetRepository.save new Tweet(id: 1, tweetedOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.status == Status.UP.code
    }

    def 'when the service has stopped tweeting for too long the health indicator shows down'() {

        setup:
        tweetRepository.save new Tweet(id: 1, tweetedOn: DateTime.now().minusDays(2))

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.status == Status.DOWN.code
    }

    def 'when the downtime period is active the health indicator shows out of service'() {

        setup:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME, activeOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.status == Status.OUT_OF_SERVICE.code
    }

    def 'when the downtime period has only just ended the health indicator still shows up'() {

        setup:
        tweetRepository.save new Tweet(id: 1, tweetedOn: DateTime.now().minusDays(2))
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME, activeOn: DateTime.now().minusSeconds(downtimePeriod).minusHours(23))

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.status == Status.UP.code
    }

    def 'if there are no tweets in the database, the health indicator shows up'() {

        setup:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME)

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.status == Status.UP.code
    }

    def 'when the final tweet is sent and the downtime period begins, the last tweet in the health indicator becomes null'() {

        setup:
        tweetRepository.save new Tweet(id: 1, sequenceNo: 1, text: 'test', tweetedOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.body.twitterBot.lastTweet.id == 1

        when:
        twitterBot.tweet()

        and:
        response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        !response.body.twitterBot.lastTweet.id
    }
}