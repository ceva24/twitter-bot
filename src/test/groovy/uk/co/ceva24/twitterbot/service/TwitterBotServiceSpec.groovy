package uk.co.ceva24.twitterbot.service

import uk.co.ceva24.twitterbot.domain.ApplicationStatus
import uk.co.ceva24.twitterbot.domain.Tweet
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class TwitterBotServiceSpec extends Specification {

    TwitterBotService twitterBotService

    def setup() {

        twitterBotService = new TwitterBotService(tweetService: Mock(TweetService), applicationStatusService: Mock(ApplicationStatusService), twitterService: Mock(TwitterService))
    }

    def 'sending a tweet updates the status in the database and sends the tweet'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        def tweet = Mock(Tweet) { getText() >> 'test' }

        when:
        twitterBotService.sendNextTweet()

        then:
        1 * twitterBotService.tweetService.nextTweet >> tweet
        1 * twitterBotService.twitterService.sendTweet('test')
        1 * tweet.setProperty('tweetedOn', new DateTime(100000))
    }

    def 'no tweet is sent if the next status is not found'() {

        when:
        twitterBotService.sendNextTweet()

        then:
        1 * twitterBotService.tweetService.nextTweet
        0 * twitterBotService.twitterService.sendTweet(_)
    }

    def "an exception thrown when attempting to update a tweet's tweeted on value is propagated"() {

        setup:
        def exception = new RuntimeException('database error')

        when:
        twitterBotService.sendNextTweet()

        then:
        twitterBotService.tweetService.nextTweet >> { throw exception }

        and:
        def e = thrown Exception
        e == exception
    }

    def 'the downtime period is not started if not all tweets have been sent'() {

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        1 * twitterBotService.tweetService.allTweetsSent() >> false
        0 * twitterBotService.applicationStatusService.downtimeStatus
        0 * twitterBotService.tweetService.resetAllTweets()
    }

    def 'starting the downtime period updates the date on the downtime status'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        twitterBotService.tweetService.allTweetsSent() >> true

        and:
        def status = Mock ApplicationStatus
        twitterBotService.applicationStatusService.downtimeStatus >> status

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        1 * status.setProperty('activeOn', new DateTime(100000))
    }

    def 'starting the downtime period reset all tweets'() {

        setup:
        twitterBotService.applicationStatusService.downtimeStatus >> Mock(ApplicationStatus)

        and:
        twitterBotService.tweetService.allTweetsSent() >> true

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        1 * twitterBotService.tweetService.resetAllTweets()
    }

    def 'attempting to start the downtime period when there is no status in the database does nothing'() {

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        0 * twitterBotService.tweetService.resetAllTweets()
    }
}