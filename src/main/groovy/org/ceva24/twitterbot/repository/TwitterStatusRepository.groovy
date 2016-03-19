package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.TwitterStatus
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TwitterStatusRepository extends CrudRepository<TwitterStatus, Long> {

    TwitterStatus findFirstByTweetedOnIsNullOrderBySequenceNoAsc()

    TwitterStatus findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()

    Long countByTweetedOnIsNull()

    @Modifying
    @Query('update TwitterStatus t set t.tweetedOn = null')
    void resetAll()
}