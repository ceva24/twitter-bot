package org.ceva24.symphonia.service

import org.ceva24.symphonia.repository.QuoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SymphoniaService {

    // TODO if no more quotes then reset all, send ending tweet and enter downtime mode

    @Autowired
    QuoteRepository quoteRepository

    @Autowired
    TwitterService tweetService

    def tweetNextQuote() {

        // TODO if in wait period throw exception

        def quote = quoteRepository.findNextQuote()

        // TODO how to handle empty database

        // TODO update quote isTweeted value here - if an exception occurs now or later we can rollback

        def tweet = tweetService.sendTweet quote.text

        return new TweetQuoteResult(timestamp: tweet.createdAt, id: tweet.id, text: tweet.text)
    }

    static class TweetQuoteResult {

        Date timestamp
        Long id
        String text
    }
}