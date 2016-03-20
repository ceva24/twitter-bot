package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class TwitterBotServiceSpec extends Specification {

    TwitterBotService twitterBotService

    def setup() {

        twitterBotService = new TwitterBotService(twitterStatusService: Mock(TwitterStatusService), configService: Mock(ConfigService), tweetService: Mock(TwitterService))
    }

    def 'sending a tweet updates the status in the database and sends the tweet'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        def status = Mock(TwitterStatus) { getText() >> 'test' }

        when:
        twitterBotService.tweetNextStatus()

        then:
        1 * twitterBotService.twitterStatusService.nextTweet >> status
        1 * twitterBotService.tweetService.sendTweet('test')
        1 * status.setProperty('tweetedOn', new DateTime(100000))
    }

    def 'no tweet is sent if the next status is not found'() {

        when:
        twitterBotService.tweetNextStatus()

        then:
        1 * twitterBotService.twitterStatusService.nextTweet
        0 * twitterBotService.tweetService.sendTweet(_)
    }

    def "an exception thrown when attempting to update a twitter status' tweeted on value is propagated"() {

        setup:
        def exception = new RuntimeException('database error')

        when:
        twitterBotService.tweetNextStatus()

        then:
        twitterBotService.twitterStatusService.nextTweet >> { throw exception }

        and:
        def e = thrown Exception
        e == exception
    }

    def 'the downtime period is not started if not all statuses have been tweeted'() {

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        1 * twitterBotService.twitterStatusService.allStatusesTweeted() >> false
        0 * twitterBotService.configService.downtimeConfig
        0 * twitterBotService.twitterStatusService.resetAllTwitterStatuses()
    }

    def 'starting the downtime period updates the date on the downtime config'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        twitterBotService.twitterStatusService.allStatusesTweeted() >> true

        and:
        def config = Mock Config
        twitterBotService.configService.downtimeConfig >> config

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        1 * config.setProperty('activeOn', new DateTime(100000))
    }

    def 'starting the downtime period reset all twitter statuses'() {

        setup:
        twitterBotService.configService.downtimeConfig >> Mock(Config)

        and:
        twitterBotService.twitterStatusService.allStatusesTweeted() >> true

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        1 * twitterBotService.twitterStatusService.resetAllTwitterStatuses()
    }

    def 'attempting to start the downtime period when there is no config in the database does nothing'() {

        when:
        twitterBotService.startDowntimePeriodIfAllStatusesTweeted()

        then:
        0 * twitterBotService.twitterStatusService.resetAllTwitterStatuses()
    }
}