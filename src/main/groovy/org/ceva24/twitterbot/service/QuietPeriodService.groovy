package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.exception.DowntimePeriodException
import org.ceva24.twitterbot.exception.QuietPeriodException
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.util.PeriodCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class QuietPeriodService {

    @Value('${org.ceva24.twitter-bot.tweet.quiet-period.seconds}')
    Long quietPeriodLength

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Long downtimePeriodLength

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    ConfigRepository configRepository

    def checkCanTweet() {

        checkDowntimePeriod()
        checkQuietPeriod()
    }

    protected def checkQuietPeriod() {

        def start = twitterStatusRepository.findLastStatus()?.tweetedOn
        if (!start) return

        def periodCalculator = new PeriodCalculator(start, quietPeriodLength)

        if (periodCalculator.isPeriodActive())
            throw new QuietPeriodException(periodCalculator.durationUntilEnd.toPeriod())
    }

    protected def checkDowntimePeriod() {

        def start = configRepository.findOne(Config.ConfigKey.DOWNTIME)?.activeOn
        if (!start) return

        def periodCalculator = new PeriodCalculator(start, downtimePeriodLength)

        if (periodCalculator.isPeriodActive())
            throw new DowntimePeriodException(periodCalculator.durationUntilEnd.toPeriod())
    }
}