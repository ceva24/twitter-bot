package org.ceva24.twitterbot.service

import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class TwitterBotService {

    @Autowired
    TwitterStatusService twitterStatusService

    @Autowired
    ConfigService configService

    @Autowired
    TwitterService tweetService

    @Transactional
    def tweetNextStatus() {

        def status = twitterStatusService.nextTweet

        if (!status) {

            log.error 'Failed to find next status to tweet'
            return
        }

        status.tweetedOn = DateTime.now()

        tweetService.sendTweet status.text

        log.info "Successfully tweeted next status: ${status}"
    }

    @Transactional
    def startDowntimePeriodIfAllStatusesTweeted() {

        if (!twitterStatusService.allStatusesTweeted()) return

        log.info 'Starting downtime period'

        def downtime = configService.downtimeConfig

        if (!downtime) {

            log.error 'Failed to find downtime configuration'
            return
        }

        downtime.activeOn = DateTime.now()

        twitterStatusService.resetAllTwitterStatuses()
    }
}