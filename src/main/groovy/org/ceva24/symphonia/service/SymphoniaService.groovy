package org.ceva24.symphonia.service

import org.ceva24.symphonia.exception.QuietPeriodException
import org.ceva24.symphonia.repository.QuoteRepository
import org.joda.time.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SymphoniaService {

    @Value('${org.ceva24.symphonia.quote.quiet-period.seconds}')
    Long quietPeriod

    // TODO if no more quotes then reset all, send ending tweet and enter downtime mode

    @Autowired
    QuoteRepository quoteRepository

    @Autowired
    TwitterService tweetService

    def tweetNextQuote() {

        checkQuietPeriod()

        def quote = quoteRepository.findNextQuote()

        // TODO how to handle empty database

        // TODO update quote isTweeted value here - if an exception occurs now or later we can rollback

        def tweet = tweetService.sendTweet quote.text

        return new TweetQuoteResult(timestamp: tweet.createdAt, id: tweet.id, text: tweet.text)
    }

    protected def checkQuietPeriod() {

        def lastTweetTime = quoteRepository.findLatestQuote()?.tweetedOn?.time

        if (!lastTweetTime) return

        def now = new Date().time

        def secondsPassedSinceLastTweet = new Period(lastTweetTime, now).toStandardSeconds().seconds

        if (secondsPassedSinceLastTweet < quietPeriod) {

            def timeRemainingMillis = (quietPeriod - secondsPassedSinceLastTweet) * 1000

            throw new QuietPeriodException(new Period(now, now + timeRemainingMillis))
        }
    }

    static class TweetQuoteResult {

        Date timestamp
        Long id
        String text
    }
}