package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.exception.DowntimePeriodException
import org.ceva24.twitterbot.exception.QuietPeriodException
import org.ceva24.twitterbot.service.QuietPeriodService
import org.ceva24.twitterbot.service.TweetService
import org.ceva24.twitterbot.service.TwitterBotService
import org.joda.time.DateTime
import org.joda.time.Period
import org.springframework.http.HttpStatus
import spock.lang.Specification

class TwitterBotControllerSpec extends Specification {

    TwitterBotController controller

    def setup() {

        controller = new TwitterBotController(quietPeriodService: Mock(QuietPeriodService), twitterBotService: Mock(TwitterBotService))
    }

    def 'requests check that the quiet period and downtime period is not active and then tweet a new status'() {

        when:
        controller.tweet()

        then:
        1 * controller.quietPeriodService.checkCanTweet()
        1 * controller.twitterBotService.tweet() >> Mock(TweetService.TweetResult)
    }

    def 'new statuses are not tweeted if the quiet period is active'() {

        when:
        controller.tweet()

        then:
        1 * controller.quietPeriodService.checkCanTweet() >> { throw new QuietPeriodException(new Period()) }
        0 * controller.twitterBotService.tweet()

        and:
        thrown QuietPeriodException
    }

    def 'new statuses are not tweeted if the downtime period is active'() {

        when:
        controller.tweet()

        then:
        1 * controller.quietPeriodService.checkCanTweet() >> { throw new DowntimePeriodException(new Period()) }
        0 * controller.twitterBotService.tweet()

        and:
        thrown DowntimePeriodException
    }

    def 'a successful status update returns the tweet data in the response body'() {

        given:
        controller.twitterBotService.tweet() >> new TweetService.TweetResult(timestamp: new DateTime(2000, 1, 1, 0, 0), id: 1, text: 'test')

        when:
        def result = controller.tweet()

        then:
        result.timestamp == new DateTime(2000, 1, 1, 0, 0)
        result.status == HttpStatus.OK.value()
        result.id == 1L
        result.text == 'test'
    }
}