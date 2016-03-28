package uk.co.ceva24.twitterbot.repository

import uk.co.ceva24.twitterbot.domain.Tweet
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TweetRepository extends CrudRepository<Tweet, Long> {

    Tweet findFirstByTweetedOnIsNullOrderBySequenceNoAsc()

    Tweet findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()

    Long countByTweetedOnIsNull()

    @Modifying
    @Query('update Tweet t set t.tweetedOn = null')
    void resetAll()
}