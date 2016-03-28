package uk.co.ceva24.twitterbot

import groovy.util.logging.Slf4j
import uk.co.ceva24.twitterbot.service.ApplicationStatusService
import uk.co.ceva24.twitterbot.service.TwitterBotService
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
    ApplicationStatusService applicationStatusService

    @Autowired
    MessageSource messageSource

    @Scheduled(cron = '0 0 17 * * *')
    void tweet() {

        if (applicationStatusService.isDowntimePeriod()) {

            def remaining = applicationStatusService.downtimePeriodTimeRemaining

            log.info "Not sending next tweet because the downtime period is active (time remaining: ${remaining.weeks}w ${remaining.days}d ${remaining.hours}h ${remaining.minutes}m ${remaining.seconds}s)"
        }
        else {

            twitterBotService.sendNextTweet()
            twitterBotService.startDowntimePeriodIfAllTweetsSent()
        }
    }
}