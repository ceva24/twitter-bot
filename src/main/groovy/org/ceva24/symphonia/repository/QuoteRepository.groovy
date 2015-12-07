package org.ceva24.symphonia.repository

import org.ceva24.symphonia.domain.Quote
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface QuoteRepository extends Repository<Quote, Long> {

    @Query('select q from Quote q where q.sequenceNo = (select min(q.sequenceNo) from Quote q where q.tweetedOn is null)')
    Quote findNextQuote()

    @Query('select q from Quote q where q.tweetedOn = (select max(q.tweetedOn) from Quote q)')
    Quote findLatestQuote()
}