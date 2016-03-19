package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.DateTime
import org.joda.time.Period
import spock.lang.Specification

class TwitterBotControllerSpec extends Specification {

    TwitterBotController controller

    def setup() {

        controller = new TwitterBotController(twitterBotService: Mock(TwitterBotService), downtimePeriodService: Mock(DowntimePeriodService))

        controller.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(0)
    }

    def 'the status shows the last tweet'() {

        given:
        controller.twitterBotService.lastTweet >> new TwitterStatus(id: 1, tweetedOn: DateTime.parse('2010-06-30T01:20'), text: 'test tweet')

        when:
        def response = controller.status()

        then:
        response.status.lastTweet.id == 1L
        response.status.lastTweet.tweetedOn == DateTime.parse('2010-06-30T01:20')
        response.status.lastTweet.text == 'test tweet'
    }

    def 'the status shows the last tweet as null when no tweets have been sent'() {

        when:
        def response = controller.status()

        then:
        !response.status.lastTweet
    }

    def 'the status shows the downtime period as inactive with no time remaining when the downtime period is not active'() {

        given:
        controller.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(0)
        controller.downtimePeriodService.isDowntimePeriod() >> false

        when:
        def response = controller.status()

        then:
        !response.status.downtime.active
        response.status.downtime.remaining == 0
    }

    def 'the status shows the downtime period as active and the time remaining when the downtime period is active'() {

        given:
        controller.downtimePeriodService.isDowntimePeriod() >> true

        when:
        def response = controller.status()

        then:
        _ * controller.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(10000)

        and:
        response.status.downtime.active
        response.status.downtime.remaining == 10
    }
}