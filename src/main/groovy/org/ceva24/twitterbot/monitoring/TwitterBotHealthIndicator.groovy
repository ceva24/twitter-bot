package org.ceva24.twitterbot.monitoring

import org.ceva24.twitterbot.domain.Tweet
import org.ceva24.twitterbot.service.ApplicationStatusService
import org.ceva24.twitterbot.service.TweetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class TwitterBotHealthIndicator implements HealthIndicator {

    @Autowired
    ApplicationStatusService applicationStatusService

    @Autowired
    TweetService tweetService

    @Override
    Health health() {

        def health = new Health.Builder()
                .withDetail('lastTweet', tweetService.lastTweet ?: new Tweet())
                .withDetail('isDowntimePeriodActive', applicationStatusService.isDowntimePeriod())
                .withDetail('downtimePeriodRemaining', applicationStatusService.downtimePeriodTimeRemaining.toStandardSeconds().seconds)

        if (applicationStatusService.isDowntimePeriod())
            return health.outOfService().build()

        // if the downtime period is active or has been active recently enough that the next tweet might not have been sent yet
        if (applicationStatusService.downtimeStatus?.activeOn?.plusSeconds(applicationStatusService.downtimePeriod)?.plusDays(1)?.afterNow)
            return health.up().build()

        // If the last tweet was sent so long ago that another one should have been sent since
        if (tweetService.lastTweet?.tweetedOn?.plusDays(1)?.beforeNow)
            return health.down().build()

        return health.up().build()
    }
}