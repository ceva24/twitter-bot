package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.Status
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface StatusRepository extends Repository<Status, Long> {

    @Query('select s from Status s where s.sequenceNo = (select min(s.sequenceNo) from Status s where s.tweetedOn is null)')
    Status findNextStatus()

    @Query('select s from Status s where s.tweetedOn = (select max(s.tweetedOn) from Status s)')
    Status findLastStatus()

    @Modifying
    @Query('update Status s set s.tweetedOn = :tweetedOn where s.id = :id')
    void save(@Param('tweetedOn') Date date, @Param('id') Long id)

    Long countByTweetedOnIsNull()

    Long countByTweetedOnIsNotNull()
}