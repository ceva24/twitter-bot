package org.ceva24.twitterbot.twitter

import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.Period
import org.springframework.context.MessageSource
import spock.lang.Specification

class TweetSenderSpec extends Specification {

    TweetSender tweetSender

    def setup() {

        tweetSender = new TweetSender(twitterBotService: Mock(TwitterBotService), downtimePeriodService: Mock(DowntimePeriodService), messageSource: Mock(MessageSource))

        tweetSender.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(10)
    }

    def 'a tweet is sent when the downtime period is not active'() {

        given:
        tweetSender.downtimePeriodService.isDowntimePeriod() >> false

        when:
        tweetSender.tweet()

        then:
        1 * tweetSender.twitterBotService.tweet()
    }

    def 'a tweet is not sent when the downtime period is active'() {

        given:
        tweetSender.downtimePeriodService.isDowntimePeriod() >> true

        when:
        tweetSender.tweet()

        then:
        0 * tweetSender.twitterBotService.tweet()
    }

    def 'the downtime period is not activated when there are statuses to be sent after the current one'() {

        setup:
        tweetSender.downtimePeriodService.isDowntimePeriod() >> false
        tweetSender.twitterBotService.allTwitterStatusesTweeted() >> false

        when:
        tweetSender.tweet()

        then:
        0 * tweetSender.downtimePeriodService.startDowntimePeriod()
    }

    def 'the downtime period is activated when tweeting the last status update'() {

        setup:
        tweetSender.downtimePeriodService.isDowntimePeriod() >> false
        tweetSender.twitterBotService.allTwitterStatusesTweeted() >> true

        when:
        tweetSender.tweet()

        then:
        1 * tweetSender.downtimePeriodService.startDowntimePeriod()
    }
}