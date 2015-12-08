package org.ceva24.twitterbot.command

import org.ceva24.twitterbot.exception.QuietPeriodException
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class TweetCommand {

    @Autowired
    TwitterBotService twitterBotService

    @Value('${org.ceva24.twitter-bot.tweet.quiet-period.seconds}')
    Long quietPeriod

    def sendNextTweet() {

        checkQuietPeriod()

        return twitterBotService.tweet()
    }

    protected def checkQuietPeriod() {

        def lastTweetTime = twitterBotService.lastTweetTime
        if (!lastTweetTime) return

        def now = new Date().time
        def secondsPassedSinceLastTweet = new Period(lastTweetTime, now).toStandardSeconds().seconds

        if (secondsPassedSinceLastTweet < quietPeriod) {

            def timeRemainingMillis = (quietPeriod - secondsPassedSinceLastTweet) * 1000

            throw new QuietPeriodException(new Period(now, now + timeRemainingMillis))
        }
    }

    protected def checkDowntimePeriod() {

    }
}