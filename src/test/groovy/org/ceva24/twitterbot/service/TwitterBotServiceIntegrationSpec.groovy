package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Ignore
import spock.lang.Specification

import javax.persistence.EntityManager

@ActiveProfiles('test')
@IntegrationTest
@Transactional
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    ConfigRepository configRepository

    @Autowired
    EntityManager entityManager

    @Ignore('test rollbacks')
    def "an exception thrown when sending a tweet does not update the twitter status' tweeted on value"() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)

        and:
        twitterBotService.tweetService = Mock TwitterService
        twitterBotService.tweetService.sendTweet(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweetNextStatus()

        then:
        thrown RuntimeException

        and:
        entityManager.clear()

        and:
        !twitterStatusRepository.findOne(1L).tweetedOn
        false
    }

    def 'starting the downtime period updates the downtime last active date'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn == new DateTime(100000)
    }

    def 'all tweets have their tweeted on values reset when the downtime period is activated'() {

        setup:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)
        twitterStatusRepository.save([new TwitterStatus(id: 1, tweetedOn: DateTime.now()), new TwitterStatus(id: 2, tweetedOn: DateTime.now())])

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        entityManager.clear()

        and:
        twitterStatusRepository.count() == 2L
        twitterStatusRepository.findAll().every { !it.tweetedOn }
    }

    @Ignore('test rollbacks')
    def "an exception thrown when resetting all twitter statuses does not update the downtime period's active on date"() {

        setup:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        and:
        twitterBotService.twitterStatusService = Mock TwitterStatusService
        twitterBotService.twitterStatusService.resetAllTwitterStatuses() >> { throw new RuntimeException() }

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        thrown RuntimeException

        and:
        entityManager.clear()

        and:
        !configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn
    }
}