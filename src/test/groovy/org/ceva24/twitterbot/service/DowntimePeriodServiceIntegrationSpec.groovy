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
import spock.lang.Specification

import javax.persistence.EntityManager

@ActiveProfiles('test')
@IntegrationTest
@Transactional
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class DowntimePeriodServiceIntegrationSpec extends Specification {

    @Autowired
    ConfigRepository configRepository

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    DowntimePeriodService downtimePeriodService

    @Autowired
    EntityManager entityManager

    def 'starting the downtime period updates the downtime last active date'() {

        setup:
        DateTimeUtils.currentMillisFixed = 10000

        and:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        when:
        downtimePeriodService.startDowntimePeriod()

        then:
        configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn == new DateTime(10000)
    }

    def 'all tweets have their tweeted on values reset when the downtime period is activated'() {

        setup:
        configRepository.save new Config(id: Config.ConfigId.DOWNTIME)
        twitterStatusRepository.save([new TwitterStatus(id: 1, tweetedOn: DateTime.now()), new TwitterStatus(id: 2, tweetedOn: DateTime.now())])

        when:
        downtimePeriodService.startDowntimePeriod()

        and:
        entityManager.clear()

        then:
        twitterStatusRepository.count() == 2L
        twitterStatusRepository.findAll().every { !it.tweetedOn }
    }
}