package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.test.TestApplication
import org.ceva24.twitterbot.test.TestConfigRepository
import org.ceva24.twitterbot.test.TestTwitterStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@ActiveProfiles('test')
@IntegrationTest
@Transactional
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    ConfigRepository configRepository

    @Autowired
    TestTwitterStatusRepository testTwitterStatusRepository

    @Autowired
    TestConfigRepository testConfigRepository

    def 'an exception thrown when activating the downtime period performs a rollback on twitter status'() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)

        and:
        twitterBotService.configRepository = Mock ConfigRepository
        twitterBotService.configRepository.setActiveOnFor(_, _) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !testTwitterStatusRepository.findOne(1).tweetedOn
    }

    def 'an exception thrown when sending a tweet performs a rollback on twitter status and config'() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)
        testConfigRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        and:
        twitterBotService.tweetService = Mock(TweetService)
        twitterBotService.tweetService.sendTweet(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !testTwitterStatusRepository.findOne(1).tweetedOn
        !configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn
    }

    def 'an exception is thrown when the next status to tweet is not found'() {

        when:
        twitterBotService.tweet()

        then:
        thrown Exception
    }
}