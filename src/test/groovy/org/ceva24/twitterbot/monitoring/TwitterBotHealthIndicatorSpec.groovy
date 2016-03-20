package org.ceva24.twitterbot.monitoring

import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.DateTime
import org.joda.time.Period
import spock.lang.Specification

class TwitterBotHealthIndicatorSpec extends Specification {

    TwitterBotHealthIndicator indicator

    def setup() {

        indicator = new TwitterBotHealthIndicator( downtimePeriod: 70, twitterBotService: Mock(TwitterBotService),
                downtimePeriodService: Mock(DowntimePeriodService), configRepository: Mock(ConfigRepository))

        indicator.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(0)
        indicator.downtimePeriodService.isDowntimePeriod() >> false

        indicator.twitterBotService.lastTweet >> new TwitterStatus()
    }

    def 'the status shows the last tweet'() {

        when:
        def response = indicator.health()

        then:
        _ * indicator.twitterBotService.lastTweet >> new TwitterStatus(id: 1, tweetedOn: DateTime.parse('2010-06-30T01:20'), text: 'test tweet')

        and:
        response.details.lastTweet.id == 1L
        response.details.lastTweet.tweetedOn == DateTime.parse('2010-06-30T01:20')
        response.details.lastTweet.text == 'test tweet'
    }

    def 'the status shows the last tweet as null when no tweets have been sent'() {

        when:
        def response = indicator.health()

        then:
        !response.details.lastTweet.id
        !response.details.lastTweet.sequenceNo
        !response.details.lastTweet.text
        !response.details.lastTweet.tweetedOn
    }

    def 'the status shows the downtime period as inactive with no time remaining when the downtime period is not active'() {

        when:
        def response = indicator.health()

        then:
        !response.details.isDowntimePeriodActive
        response.details.downtimePeriodRemaining == 0
    }

    def 'the status shows the downtime period as active and the time remaining when the downtime period is active'() {

        when:
        def response = indicator.health()

        then:
        _ * indicator.downtimePeriodService.isDowntimePeriod() >> true
        _ * indicator.downtimePeriodService.downtimePeriodTimeRemaining >> new Period(10000)

        and:
        response.details.isDowntimePeriodActive
        response.details.downtimePeriodRemaining == 10
    }
}