package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.repository.ConfigRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.Period
import org.joda.time.PeriodType
import spock.lang.Specification

class ConfigServiceSpec extends Specification {

    ConfigService configService

    def setup() {

        configService = new ConfigService(configRepository: Mock(ConfigRepository), downtimePeriodLength: 60)
    }

    def 'getting the downtime config finds it in the repository'() {

        given:
        def config = Mock(Config)

        when:
        def result = configService.downtimeConfig

        then:
        1 * configService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> config

        and:
        result == config
    }

    def 'the downtime period is active when the time since the downtime period began is less than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        configService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        configService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when the time since the downtime period last began is greater than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        configService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        !configService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when it has never been triggered'() {

        expect:
        !configService.isDowntimePeriod()
    }

    def 'the downtime period time remaining when the downtime period is active is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        configService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        configService.downtimePeriodTimeRemaining == new Period(1000).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period time remaining is 0 when the period is no longer active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        configService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> new Config(activeOn: new DateTime(100000))

        expect:
        configService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period remaining is 0 when the downtime period has never been triggered'() {

        expect:
        configService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }
}