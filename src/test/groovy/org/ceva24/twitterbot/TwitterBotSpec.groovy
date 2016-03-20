package org.ceva24.twitterbot

import org.ceva24.twitterbot.service.ConfigService
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.Period
import org.springframework.context.MessageSource
import spock.lang.Specification

class TwitterBotSpec extends Specification {

    TwitterBot tweetSender

    def setup() {

        tweetSender = new TwitterBot(twitterBotService: Mock(TwitterBotService), configService: Mock(ConfigService), messageSource: Mock(MessageSource))

        tweetSender.configService.downtimePeriodTimeRemaining >> new Period(10)
    }

    def 'a tweet is sent and the downtime period is started if necessary when the downtime period is not active'() {

        when:
        tweetSender.tweet()

        then:
        1 * tweetSender.twitterBotService.tweetNextStatus()
        1 * tweetSender.twitterBotService.startDowntimePeriodIfAllStatusesTweeted()
    }

    def 'a tweet is not sent when the downtime period is active'() {

        given:
        tweetSender.configService.isDowntimePeriod() >> true

        when:
        tweetSender.tweet()

        then:
        0 * tweetSender.twitterBotService.tweetNextStatus()
    }
}