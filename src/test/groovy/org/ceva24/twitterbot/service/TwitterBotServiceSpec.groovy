package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class TwitterBotServiceSpec extends Specification {

    TwitterBotService twitterBotService

    def setup() {

        twitterBotService = new TwitterBotService(twitterStatusRepository: Mock(TwitterStatusRepository), configRepository: Mock(ConfigRepository),
                tweetService: Mock(TweetService))

        twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> new TwitterStatus(id: 1)
    }

    def 'sending a tweet updates the status in the database and sends the tweet'() {

        setup:
        twitterBotService.configRepository.findOne(_) >> Mock(Config)

        and:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        def status = Mock(TwitterStatus) { getText() >> 'test' }

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> status
        1 * twitterBotService.tweetService.sendTweet('test') >> Mock(Tweet) { getTweetedOn() >> new DateTime(100000) }
        1 * status.setProperty('tweetedOn', new DateTime(100000))
    }

    def 'the downtime period is not activated when there are statuses to be sent after the current one'() {

        setup:
        twitterBotService.tweetService.sendTweet(_) >> Mock(Tweet)

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 2
        0 * twitterBotService.twitterStatusRepository.resetAll()
        0 * twitterBotService.configRepository.findOne(_)
    }

    def 'the downtime period is activated when tweeting the last status update'() {

        setup:
        twitterBotService.tweetService.sendTweet(_) >> Mock(Tweet)

        and:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        def config = Mock Config

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 1
        1 * twitterBotService.twitterStatusRepository.resetAll()
        1 * twitterBotService.configRepository.findOne(Config.ConfigId.DOWNTIME) >> config
        1 * config.setProperty('activeOn', new DateTime(100000))
    }

    def 'an exception is thrown if the next status is not found'() {

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> null

        and:
        thrown NullPointerException
    }

    def "an exception thrown when attempting to update a twitter status' tweeted on value is propagated"() {

        setup:
        def exception = new RuntimeException('database error')

        when:
        twitterBotService.tweet()

        then:
        twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> { throw exception }

        and:
        def e = thrown Exception
        e == exception
    }

    def 'getting the last tweet searches the repository'() {

        when:
        twitterBotService.lastTweet

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc()
    }
}