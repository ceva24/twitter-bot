package uk.co.ceva24.twitterbot.service

import uk.co.ceva24.twitterbot.repository.TweetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TweetService {

    @Autowired
    TweetRepository tweetRepository

    def getNextTweet() {

        return tweetRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc()
    }

    def getLastTweet() {

        return tweetRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()
    }

    def allTweetsSent() {

        return (tweetRepository.countByTweetedOnIsNull() <= 0L)
    }

    def resetAllTweets() {

        tweetRepository.resetAll()
    }
}