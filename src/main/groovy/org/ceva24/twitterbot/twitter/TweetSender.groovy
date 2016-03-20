package org.ceva24.twitterbot.twitter

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class TweetSender {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    DowntimePeriodService downtimePeriodService

    @Autowired
    MessageSource messageSource

    @Scheduled(cron = '0/15 * * * * *')
    void tweet() {

        if (downtimePeriodService.isDowntimePeriod()) {

            def remaining = downtimePeriodService.downtimePeriodTimeRemaining

            log.info "The downtime period is active (time remaining: ${remaining.weeks}w ${remaining.days}d ${remaining.hours}h ${remaining.minutes}m ${remaining.seconds}s)"
        }
        else {

            def tweet = twitterBotService.tweet()

            log.info "Sent tweet: ${tweet}"

            if (twitterBotService.allTwitterStatusesTweeted()) {

                downtimePeriodService.startDowntimePeriod()

                log.info 'Activated downtime mode'
            }
        }
    }
}