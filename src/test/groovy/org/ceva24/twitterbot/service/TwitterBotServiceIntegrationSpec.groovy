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
import spock.lang.Specification

@ActiveProfiles('test')
@IntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TestTwitterStatusRepository testTwitterStatusRepository

    @Autowired
    TestConfigRepository testConfigRepository

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    ConfigRepository configRepository

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
        testConfigRepository.save new Config(id: Config.ConfigKey.DOWNTIME)

        and:
        twitterBotService.tweetService = Mock(TweetService)
        twitterBotService.tweetService.sendTweet(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !testTwitterStatusRepository.findOne(1).tweetedOn
        !configRepository.findOne(Config.ConfigKey.DOWNTIME).activeOn
    }
}