package org.ceva24.twitterbot.controller

import org.apache.tomcat.util.codec.binary.Base64
import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.test.TestApplication
import org.ceva24.twitterbot.test.TestConfigRepository
import org.ceva24.twitterbot.test.TestTwitterStatusRepository
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
import spock.lang.Specification
import spock.lang.Unroll

@ActiveProfiles('test')
@WebIntegrationTest(randomPort = true)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class TwitterBotControllerIntegrationSpec extends Specification {

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
    TestTwitterStatusRepository testTwitterStatusRepository

    @Autowired
    TestConfigRepository testConfigRepository

    RestTemplate restTemplate
    HttpHeaders authenticationHeader

    def setup() {

        restTemplate = new TestRestTemplate()

        authenticationHeader = new HttpHeaders()
        authenticationHeader.add 'Authorization', "Basic ${new String(Base64.encodeBase64("${username}:${password}".bytes))}"
    }

    def cleanup() {

        testTwitterStatusRepository.deleteAll()
        testConfigRepository.deleteAll()
    }

    def 'unauthenticated requests returns http unauthorized'() {

        when:
        def response = restTemplate.getForEntity "http://localhost:${port}/", Map

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED

        and:
        response.body.status == HttpStatus.UNAUTHORIZED.value()
    }

    def 'the last tweet status is null when no tweets have been sent'() {

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        !response.body.status.lastTweet
    }

    def 'the last tweet status shows the last tweet that was sent'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1)

        when:
        tweetSender.tweet()

        and:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.status.lastTweet.timestamp == new DateTime(100000, DateTimeZone.UTC).toString()
        response.body.status.lastTweet.id == 1
        response.body.status.lastTweet.text == 'test 1'
    }

    def 'the status correctly shows when the downtime period is inactive'() {

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        !response.body.status.downtime.active
        response.body.status.downtime.remaining == 0
    }

    def 'the status shows when the downtime period is active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        testConfigRepository.save(new Config(id: Config.ConfigId.DOWNTIME, activeOn: DateTime.now()))

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.status.downtime.active
        response.body.status.downtime.remaining == downtimePeriod
    }

    @Unroll
    def 'an authenticated request using http #method returns http method not allowed'() {

        when:
        def response = restTemplate.exchange  "http://localhost:${port}/", method, new HttpEntity<String>(authenticationHeader), Map

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