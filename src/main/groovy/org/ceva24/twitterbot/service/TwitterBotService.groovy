package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TwitterBotService {

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

        return tweetService.sendTweet(status.text)
    }

    protected def updateStatusTweetedOn(TwitterStatus status) {

        twitterStatusRepository.setTweetedOnFor new DateTime(), status.id
    }

    protected def activateDowntimeModeIfComplete() {

        if (twitterStatusRepository.countByTweetedOnIsNull() > 0) return

        twitterStatusRepository.resetAll()

        configRepository.setActiveOnFor new DateTime(), Config.ConfigKey.DOWNTIME
    }
}