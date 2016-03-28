package uk.co.ceva24.twitterbot

import uk.co.ceva24.twitterbot.service.ApplicationStatusService
import uk.co.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.Period
import org.springframework.context.MessageSource
import spock.lang.Specification

class TwitterBotSpec extends Specification {

    TwitterBot twitterBot

    def setup() {

        twitterBot = new TwitterBot(twitterBotService: Mock(TwitterBotService), applicationStatusService: Mock(ApplicationStatusService), messageSource: Mock(MessageSource))

        twitterBot.applicationStatusService.downtimePeriodTimeRemaining >> new Period(10)
    }

    def 'a tweet is sent and the downtime period is started if necessary when the downtime period is not active'() {

        when:
        twitterBot.tweet()

        then:
        1 * twitterBot.twitterBotService.sendNextTweet()
        1 * twitterBot.twitterBotService.startDowntimePeriodIfAllTweetsSent()
    }

    def 'a tweet is not sent when the downtime period is active'() {

        given:
        twitterBot.applicationStatusService.isDowntimePeriod() >> true

        when:
        twitterBot.tweet()

        then:
        0 * twitterBot.twitterBotService.sendNextTweet()
    }
}