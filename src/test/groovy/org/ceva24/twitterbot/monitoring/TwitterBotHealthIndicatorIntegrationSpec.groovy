package org.ceva24.twitterbot.monitoring

import org.apache.tomcat.util.codec.binary.Base64
import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.twitter.TweetSender
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeZone
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
import spock.lang.Unroll

@ActiveProfiles('test')
@WebIntegrationTest(randomPort = true)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotHealthIndicatorIntegrationSpec {

    @Value('${local.server.port}')
    int port

    @Value('${security.user.name}')
    String username

    @Value('${security.user.password}')
    String password

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Integer downtimePeriod

    @Autowired
    TweetSender tweetSender

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    ConfigRepository configRepository

    RestTemplate restTemplate
    HttpHeaders authenticationHeader

    def setup() {

        restTemplate = new TestRestTemplate()

        authenticationHeader = new HttpHeaders()
        authenticationHeader.add 'Authorization', "Basic ${new String(Base64.encodeBase64("${username}:${password}".bytes))}"

        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)
    }

    def cleanup() {

        twitterStatusRepository.deleteAll()
        configRepository.deleteAll()
    }

    def 'unauthenticated requests returns http unauthorized'() {

        when:
        def response = restTemplate.getForEntity "http://localhost:${port}/health", Map

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED

        and:
        response.body.status == HttpStatus.UNAUTHORIZED.value()
    }

    def 'the last tweet status is null when no tweets have been sent'() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1)

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        !response.body.twitterBot.lastTweet.id
    }

    def 'the last tweet status shows the last tweet that was sent'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1)

        when:
        tweetSender.tweet()

        and:
        def response = restTemplate.exchange "http://localhost:${port}/helath", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.twitterBot.lastTweet.tweetedOn == new DateTime(100000, DateTimeZone.UTC).toString()
        response.body.twitterBot.lastTweet.id == 1
        response.body.twitterBot.lastTweet.text == 'test 1'
    }

    def 'the status correctly shows when the downtime period is inactive'() {

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        !response.body.twitterBot.downtime.active
        response.body.twitterBot.downtime.remaining == 0
    }

    def 'the status shows when the downtime period is active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME, activeOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/health", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.twitterBot.downtime.active
        response.body.twitterBot.downtime.remaining == downtimePeriod
    }

    @Unroll
    def 'an authenticated request using http #method returns http method not allowed'() {

        when:
        def response = restTemplate.exchange  "http://localhost:${port}/twitterBot", method, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.METHOD_NOT_ALLOWED

        and:
        response.body.status == HttpStatus.METHOD_NOT_ALLOWED.value()
        response.body.error == HttpStatus.METHOD_NOT_ALLOWED.reasonPhrase
        !response.body.exception

        where:
        method            | _
        HttpMethod.POST   | _
        HttpMethod.PUT    | _
        HttpMethod.DELETE | _
    }
}