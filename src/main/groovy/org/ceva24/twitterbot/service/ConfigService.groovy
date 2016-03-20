package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.util.PeriodCalculator
import org.joda.time.Period
import org.joda.time.PeriodType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigService {

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Long downtimePeriodLength

    @Autowired
    ConfigRepository configRepository

    def getDowntimeConfig() {

        return configRepository.findOne(Config.ConfigId.DOWNTIME)
    }

    def isDowntimePeriod() {

        def start = configRepository.findOne(Config.ConfigId.DOWNTIME)?.activeOn

        return (start ? new PeriodCalculator(start, downtimePeriodLength).isPeriodActive() : false)
    }

    def getDowntimePeriodTimeRemaining() {

        def start = configRepository.findOne(Config.ConfigId.DOWNTIME)?.activeOn

        def period = (start ? new PeriodCalculator(start, downtimePeriodLength).durationUntilEnd.toPeriod() : new Period(0))

        return period.normalizedStandard(PeriodType.yearWeekDayTime())
    }
}