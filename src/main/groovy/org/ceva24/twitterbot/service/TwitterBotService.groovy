package org.ceva24.twitterbot.service

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TwitterBotService {

    @Autowired
    TwitterStatusService twitterStatusService

    @Autowired
    ConfigService configService

    @Autowired
    TwitterService tweetService

    // TODO manually test/handle rollbacks + integration test

    @Transactional
    def tweetNextStatus() {

        def status = twitterStatusService.nextTweet

        if (!status) return

        status.tweetedOn = DateTime.now()

        tweetService.sendTweet status.text
    }

    @Transactional
    def startDowntimePeriodIfAllStatusesTweeted() {

        if (!twitterStatusService.allStatusesTweeted()) return

        def downtime = configService.downtimeConfig

        if (!downtime) return

        downtime.activeOn = new DateTime()

        twitterStatusService.resetAllTwitterStatuses()
    }
}