package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TwitterStatusService {

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    def getNextTweet() {

        return twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc()
    }

    def getLastTweet() {

        return twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()
    }

    def allStatusesTweeted() {

        return (twitterStatusRepository.countByTweetedOnIsNull() <= 0L)
    }

    def resetAllTwitterStatuses() {

        twitterStatusRepository.resetAll()
    }
}