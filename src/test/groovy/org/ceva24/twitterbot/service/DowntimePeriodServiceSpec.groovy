package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.Period
import org.joda.time.PeriodType
import spock.lang.Specification

class DowntimePeriodServiceSpec extends Specification {

    DowntimePeriodService downtimePeriodService

    def setup() {

        downtimePeriodService = new DowntimePeriodService(twitterStatusRepository: Mock(TwitterStatusRepository), configRepository: Mock(ConfigRepository))
    }

    def 'the downtime period is active when the time since the downtime period began is less than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        downtimePeriodService.downtimePeriodLength = 60
        downtimePeriodService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        downtimePeriodService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when the time since the downtime period last began is greater than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        downtimePeriodService.downtimePeriodLength = 60
        downtimePeriodService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        !downtimePeriodService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when it has never been triggered'() {

        expect:
        !downtimePeriodService.isDowntimePeriod()
    }

    def 'the downtime period time remaining when the downtime period is active is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        downtimePeriodService.downtimePeriodLength = 60
        downtimePeriodService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        downtimePeriodService.downtimePeriodTimeRemaining == new Period(1000).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period time remaining is 0 when the period is no longer active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        downtimePeriodService.downtimePeriodLength = 60
        downtimePeriodService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        downtimePeriodService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period remaining is 0 when the downtime period has never been triggered'() {

        expect:
        downtimePeriodService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }
}