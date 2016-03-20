package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
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
    def tweet() { // TODO move to tweetservice.sendNextTweet, change this to a twitterstatusservice

        def status = twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc()

        if (!status) return

        def tweet = tweetService.sendTweet status.text

        status.tweetedOn = tweet.tweetedOn

        return status
    }

    def getLastTweet() {

        return twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc() ?: new TwitterStatus()
    }

    def allTwitterStatusesTweeted() {

        return (twitterStatusRepository.countByTweetedOnIsNull() <= 0L)
    }
}