package org.ceva24.twitterbot.monitoring

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class TwitterBotHealthIndicator implements HealthIndicator {

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Integer downtimePeriod

    @Autowired
    DowntimePeriodService downtimePeriodService

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    ConfigRepository configRepository

    @Override
    Health health() {

        def health = new Health.Builder()
                .withDetail('lastTweet', twitterBotService.lastTweet)
                .withDetail('isDowntimePeriodActive', downtimePeriodService.isDowntimePeriod())
                .withDetail('downtimePeriodRemaining', downtimePeriodService.downtimePeriodTimeRemaining.toStandardSeconds().seconds)

        if (downtimePeriodService.isDowntimePeriod())
            return health.outOfService().build()

        // if the downtime period is active or has been active recently enough that the next tweet might not have been sent yet
        if (configRepository.findOne(Config.ConfigId.DOWNTIME)?.activeOn?.plusSeconds(downtimePeriod)?.plusDays(1)?.afterNow)
            return health.up().build()

        // If the last tweet was sent so long ago that another one should have been sent since
        if (!(twitterBotService.lastTweet?.tweetedOn?.plusDays(1)?.afterNow))
            return health.down().build()

        return health.up().build()
    }
}