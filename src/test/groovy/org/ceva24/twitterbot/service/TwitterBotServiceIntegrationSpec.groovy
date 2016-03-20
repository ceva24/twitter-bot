package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.ApplicationStatus
import org.ceva24.twitterbot.domain.Tweet
import org.ceva24.twitterbot.repository.ApplicationStatusRepository
import org.ceva24.twitterbot.repository.TweetRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager

@ActiveProfiles('test')
@IntegrationTest
@Transactional
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
class TwitterBotServiceIntegrationSpec extends Specification {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    TweetRepository tweetRepository

    @Autowired
    ApplicationStatusRepository applicationStatusRepository

    @Autowired
    EntityManager entityManager

    def "an exception thrown when sending a tweet does not update the tweet's tweeted on value"() {

        setup:
        tweetRepository.save new Tweet(id: 1, text: 'test 1', sequenceNo: 1)
        entityManager.flush()

        and:
        twitterBotService.twitterService = Mock(TwitterService) { sendTweet(_) >> { throw new RuntimeException() }}

        when:
        twitterBotService.sendNextTweet()

        then:
        thrown RuntimeException

        and:
        entityManager.clear()

        and:
        !tweetRepository.findOne(1L).tweetedOn
    }

    def 'starting the downtime period updates the downtime last active date'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME)

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME).activeOn == new DateTime(100000)
    }

    def 'all tweets have their tweeted on values reset when the downtime period is activated'() {

        setup:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME)
        tweetRepository.save([new Tweet(id: 1, tweetedOn: DateTime.now()), new Tweet(id: 2, tweetedOn: DateTime.now())])

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        entityManager.clear()

        and:
        tweetRepository.count() == 2L
        tweetRepository.findAll().every { !it.tweetedOn }
    }

    def "an exception thrown when resetting all tweets does not update the downtime period's active on date"() {

        setup:
        applicationStatusRepository.save new ApplicationStatus(id: ApplicationStatus.ApplicationStatusId.DOWNTIME)
        entityManager.flush()

        and:
        twitterBotService.tweetService = Mock(TweetService) { allTweetsSent() >> true; resetAllTweets() >> { throw new RuntimeException() }}

        when:
        twitterBotService.startDowntimePeriodIfAllTweetsSent()

        then:
        thrown RuntimeException

        and:
        entityManager.clear()

        and:
        !applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME).activeOn
    }
}