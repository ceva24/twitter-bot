package org.ceva24.twitterbot

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.service.ConfigService
import org.ceva24.twitterbot.service.TwitterBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class TwitterBot {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    ConfigService configService

    @Autowired
    MessageSource messageSource

    @Scheduled(cron = '0 0 21 * * *')
    void tweet() {

        if (configService.isDowntimePeriod()) {

            def remaining = configService.downtimePeriodTimeRemaining

            log.info "Not sending next tweet because the downtime period is active (time remaining: ${remaining.weeks}w ${remaining.days}d ${remaining.hours}h ${remaining.minutes}m ${remaining.seconds}s)"
        }
        else {

            twitterBotService.tweetNextStatus()
            twitterBotService.startDowntimePeriodIfAllStatusesTweeted()
        }
    }
}