package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TwitterBotService {

    Tweet lastTweet

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    ConfigRepository configRepository

    @Autowired
    TweetService tweetService

    @Transactional
    def tweet() {

        def status = twitterStatusRepository.findNextStatus()

        updateStatusTweetedOn status

        activateDowntimeModeIfComplete()

        def tweet = tweetService.sendTweet status.text

        lastTweet = tweet

        return tweet
    }

    protected def updateStatusTweetedOn(TwitterStatus status) {

        twitterStatusRepository.setTweetedOnFor new DateTime(), status.id
    }

    protected def activateDowntimeModeIfComplete() {

        if (twitterStatusRepository.countByTweetedOnIsNull() > 0) return

        twitterStatusRepository.resetAll()

        configRepository.setActiveOnFor new DateTime(), Config.ConfigId.DOWNTIME
    }
}