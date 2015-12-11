package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.TwitterStatus
import org.joda.time.DateTime
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface TwitterStatusRepository extends Repository<TwitterStatus, Long> {

    @Query('select t from TwitterStatus t where t.sequenceNo = (select min(s.sequenceNo) from TwitterStatus s where s.tweetedOn is null)')
    TwitterStatus findNextStatus()

    @Query('select t from TwitterStatus t where t.tweetedOn = (select max(s.tweetedOn) from TwitterStatus s)')
    TwitterStatus findLastStatus()

    @Modifying
    @Query('update TwitterStatus t set t.tweetedOn = :tweetedOn where t.id = :id')
    void setTweetedOnFor(@Param('tweetedOn') DateTime date, @Param('id') Long id)

    @Modifying
    @Query('update TwitterStatus t set t.tweetedOn = null')
    void resetAll()

    Long countByTweetedOnIsNull()
}