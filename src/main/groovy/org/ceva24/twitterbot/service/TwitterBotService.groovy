package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
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

        def status = twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc()

        activateDowntimeModeIfComplete()

        def tweet = tweetService.sendTweet status.text

        status.tweetedOn = tweet.tweetedOn

        return tweet
    }

    def getLastTweet() {

        return twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()
    }

    protected def activateDowntimeModeIfComplete() {

        if (twitterStatusRepository.countByTweetedOnIsNull() > 1) return

        twitterStatusRepository.resetAll()

        def downtime = configRepository.findOne Config.ConfigId.DOWNTIME
        downtime.activeOn = new DateTime()
    }
}