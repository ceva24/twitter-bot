package org.ceva24.twitterbot.controller

import org.apache.tomcat.util.codec.binary.Base64
import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.service.TweetService
import org.ceva24.twitterbot.service.TwitterBotService
import org.ceva24.twitterbot.test.TestApplication
import org.ceva24.twitterbot.test.TestConfigRepository
import org.ceva24.twitterbot.test.TestTwitterStatusRepository
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
import org.springframework.social.DuplicateStatusException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@WebIntegrationTest(['server.port=0', 'management.port=0'])
@ActiveProfiles('test')
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class TwitterBotControllerIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    int port

    @Value('${local.management.port}')
    int managementPort

    @Value('${security.user.name}')
    String username

    @Value('${security.user.password}')
    String password

    @Autowired
    TwitterBotService twitterBotService

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

    def 'an unauthenticated request returns http unauthorized'() {

        when:
        def response = restTemplate.getForEntity "http://localhost:${port}/", Map

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED

        and:
        response.body.status == HttpStatus.UNAUTHORIZED.value()
    }

    def 'an unauthenticated request to the info page is successful'() {

        expect:
        restTemplate.getForEntity("http://localhost:${managementPort}/info", Map).statusCode == HttpStatus.OK
}

    def 'an unauthenticated request to the health page is successful'() {

        expect:
        restTemplate.getForEntity("http://localhost:${managementPort}/health", Map).statusCode == HttpStatus.OK
    }

    def 'an authenticated request to send a tweet is successful'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test', sequenceNo: 1)

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.timestamp == new DateTime(100000, DateTimeZone.UTC).toString()
        response.body.status == HttpStatus.OK.value()
        response.body.id == 1
        response.body.text == 'test'
    }

    def 'an authenticated request to send a tweet during the quiet period returns http bad request'() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test', sequenceNo: 1, tweetedOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        response.body.status == HttpStatus.BAD_REQUEST.value()
        response.body.error == HttpStatus.BAD_REQUEST.reasonPhrase
        response.body.message.contains 'Cannot send tweet during quiet period'
        !response.body.exception
    }

    def 'an authenticated request to send a tweet during the downtime period returns http bad request'() {

        setup:
        testConfigRepository.save new Config(id: Config.ConfigId.DOWNTIME, activeOn: DateTime.now())

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        response.body.status == HttpStatus.BAD_REQUEST.value()
        response.body.error == HttpStatus.BAD_REQUEST.reasonPhrase
        response.body.message.contains 'Cannot send tweet during downtime period'
        !response.body.exception
    }

    def 'an authenticated request to send a duplicate tweet returns http bad request'() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test', sequenceNo: 1)

        and:
        twitterBotService.tweetService = Mock(TweetService) { sendTweet(_) >> { throw new DuplicateStatusException('twitter', 'duplicate status error') } }

        when:
        def response = restTemplate.exchange "http://localhost:${port}/", HttpMethod.GET, new HttpEntity<String>(authenticationHeader), Map

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        response.body.status == HttpStatus.BAD_REQUEST.value()
        response.body.error == HttpStatus.BAD_REQUEST.reasonPhrase
        response.body.message == 'duplicate status error'
        !response.body.exception
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