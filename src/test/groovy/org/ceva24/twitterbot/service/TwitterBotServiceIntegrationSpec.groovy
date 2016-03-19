package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
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
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    ConfigRepository configRepository

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    def 'an exception thrown when activating the downtime period performs a rollback on twitter status'() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)

        and:
        twitterBotService.configRepository = Mock ConfigRepository
        twitterBotService.configRepository.findOne(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !twitterStatusRepository.findOne(1L).tweetedOn
    }

    def 'an exception thrown when sending a tweet performs a rollback on twitter status and config'() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        and:
        twitterBotService.tweetService = Mock TweetService
        twitterBotService.tweetService.sendTweet(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !twitterStatusRepository.findOne(1L).tweetedOn
        !configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn
    }

    def 'an exception is thrown when the next status to tweet is not found'() {

        when:
        twitterBotService.tweet()

        then:
        thrown Exception
    }

    def 'getting the last tweet gets the most recent tweet from the database'() {

        setup:
        def status = twitterStatusRepository.save new TwitterStatus(id: 1, tweetedOn: DateTime.now())
        twitterStatusRepository.save new TwitterStatus(id: 1, tweetedOn: DateTime.now().minusDays(1))

        expect:
        twitterBotService.lastTweet == status
    }
}