package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.exception.DowntimePeriodException
import org.ceva24.twitterbot.exception.QuietPeriodException
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class QuietPeriodServiceSpec extends Specification {

    QuietPeriodService quietPeriodService

    def setup() {

        quietPeriodService = new QuietPeriodService(twitterStatusRepository: Mock(TwitterStatusRepository), configRepository: Mock(ConfigRepository))
    }

    def 'no quiet period exception is thrown when the quiet period is not active'() {

        setup:
        quietPeriodService.quietPeriodLength = 60

        DateTimeUtils.setCurrentMillisFixed 161000
        quietPeriodService.twitterStatusRepository.findLastStatus() >> new TwitterStatus(tweetedOn: new DateTime(100000))

        when:
        quietPeriodService.checkCanTweet()

        then:
        notThrown QuietPeriodException
    }

    def 'a quiet period exception is thrown when the quiet period is active'() {

        setup:
        quietPeriodService.quietPeriodLength = 60

        DateTimeUtils.setCurrentMillisFixed 159000
        quietPeriodService.twitterStatusRepository.findLastStatus() >> new TwitterStatus(tweetedOn: new DateTime(100000))

        when:
        quietPeriodService.checkCanTweet()

        then:
        thrown QuietPeriodException
    }

    def 'no quiet period exception is thrown when the start is null'() {

        setup:
        quietPeriodService.quietPeriodLength = 60
        DateTimeUtils.setCurrentMillisFixed 100000

        when:
        quietPeriodService.checkCanTweet()

        then:
        notThrown QuietPeriodException
    }

    def 'no downtime period exception is thrown when the downtime period is not active'() {

        setup:
        quietPeriodService.downtimePeriodLength = 60

        DateTimeUtils.setCurrentMillisFixed 161000
        quietPeriodService.configRepository.findOne(Config.ConfigKey.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        when:
        quietPeriodService.checkCanTweet()

        then:
        notThrown DowntimePeriodException
    }

    def 'a downtime period exception is thrown when the downtime period is active'() {

        setup:
        quietPeriodService.downtimePeriodLength = 60

        DateTimeUtils.setCurrentMillisFixed 159000
        quietPeriodService.configRepository.findOne(Config.ConfigKey.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        when:
        quietPeriodService.checkCanTweet()

        then:
        thrown DowntimePeriodException
    }

    def 'no downtime period exception is thrown when the activeOn date is null'() {

        setup:
        quietPeriodService.downtimePeriodLength = 60

        DateTimeUtils.setCurrentMillisFixed 100000

        when:
        quietPeriodService.checkCanTweet()

        then:
        notThrown DowntimePeriodException
    }
}