package uk.co.ceva24.twitterbot.service

import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class TwitterBotService {

    @Autowired
    TweetService tweetService

    @Autowired
    ApplicationStatusService applicationStatusService

    @Autowired
    TwitterService twitterService

    @Transactional
    def sendNextTweet() {

        def tweet = tweetService.nextTweet

        if (!tweet) {

            log.error 'Failed to find next tweet to send'
            return
        }

        tweet.tweetedOn = DateTime.now()

        twitterService.sendTweet tweet.text

        log.info "Successfully sent next tweet: ${tweet}"
    }

    @Transactional
    def startDowntimePeriodIfAllTweetsSent() {

        if (!tweetService.allTweetsSent()) return

        log.info 'Starting downtime period'

        def downtime = applicationStatusService.downtimeStatus

        if (!downtime) {

            log.error 'Failed to find downtime status'
            return
        }

        downtime.activeOn = DateTime.now()

        tweetService.resetAllTweets()
    }
}