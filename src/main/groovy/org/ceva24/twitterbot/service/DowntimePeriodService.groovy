package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.util.PeriodCalculator
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DowntimePeriodService {

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Long downtimePeriodLength

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    ConfigRepository configRepository

    def isDowntimePeriod() {

        def start = configRepository.findOne(Config.ConfigId.DOWNTIME)?.activeOn

        return (start ? new PeriodCalculator(start, downtimePeriodLength).isPeriodActive() : false)
    }

    def getDowntimePeriodTimeRemaining() {

        def start = configRepository.findOne(Config.ConfigId.DOWNTIME)?.activeOn

        def period = (start ? new PeriodCalculator(start, downtimePeriodLength).durationUntilEnd.toPeriod() : new Period(0))

        return period.normalizedStandard(PeriodType.yearWeekDayTime())
    }

    @Transactional
    def startDowntimePeriod() { // TODO move reset all to twitterstatusservice, called by tweetsender after activating downtime period - think about rollbacks

        def downtime = configRepository.findOne Config.ConfigId.DOWNTIME

        if (!downtime) return

        downtime.activeOn = new DateTime()

        twitterStatusRepository.resetAll()
    }
}