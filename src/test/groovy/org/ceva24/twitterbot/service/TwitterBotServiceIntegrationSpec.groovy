package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@ActiveProfiles('test')
@IntegrationTest
@Transactional
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    def "an exception thrown when sending a tweet does not update the twitter status' tweeted on value"() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test1', sequenceNo: 1)

        and:
        twitterBotService.tweetService = Mock TweetService
        twitterBotService.tweetService.sendTweet(_) >> { throw new RuntimeException() }

        when:
        twitterBotService.tweet()

        then:
        thrown RuntimeException

        and:
        !twitterStatusRepository.findOne(1L).tweetedOn
    }

    def 'getting the last tweet gets the most recent tweet from the database'() {

        setup:
        twitterStatusRepository.save([new TwitterStatus(id: 1, tweetedOn: DateTime.now()), new TwitterStatus(id: 1, tweetedOn: DateTime.now().minusDays(1))])

        expect:
        twitterBotService.lastTweet.id == 1L
    }

    def 'all twitter statuses tweeted is true when there are no statuses left to tweet in the database'() {

        expect:
        twitterBotService.allTwitterStatusesTweeted()
    }

    def 'all twitter statuses tweeted is false when there are statuses left to tweet in the database'() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1)

        expect:
        !twitterBotService.allTwitterStatusesTweeted()
    }
}