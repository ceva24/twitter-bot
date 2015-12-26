package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.test.TestApplication
import org.ceva24.twitterbot.test.TestTwitterStatusRepository
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager

@IntegrationTest
@Transactional
@ActiveProfiles('test')
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class TwitterStatusRepositorySpec extends Specification {

    @Autowired
    EntityManager entityManager

    @Autowired
    TestTwitterStatusRepository testTwitterStatusRepository

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    def 'getting the next status to tweet is correct when there is a single status that has not yet been tweeted'() {

        setup:
        def status = new TwitterStatus(id: 1, text: 'test', sequenceNo: 1)
        testTwitterStatusRepository.save status

        expect:
        twitterStatusRepository.findNextStatus() == status
    }

    def 'getting the next status to tweet is correct when there are multiple statuses that have not yet been tweeted'() {

        setup:
        def status1 = new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1)
        def status2 = new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2)
        testTwitterStatusRepository.save status1
        testTwitterStatusRepository.save status2

        expect:
        twitterStatusRepository.findNextStatus() == status1
    }

    def 'getting the next status to tweet returns null when there are no statuses that have not yet been tweeted'() {

        expect:
        !twitterStatusRepository.findNextStatus()
    }

    def "getting the next status to tweet is correct when there is one status that has and one status that hasn't been tweeted"() {

        setup:
        def status1 = new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: new DateTime())
        def status2 = new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2)
        testTwitterStatusRepository.save status1
        testTwitterStatusRepository.save status2

        expect:
        twitterStatusRepository.findNextStatus() == status2
    }

    def 'getting the last status tweeted is correct where there is a single status has been tweeted'() {

        setup:
        def status = new TwitterStatus(id: 1, text: 'test', sequenceNo: 1, tweetedOn: new DateTime())
        testTwitterStatusRepository.save status

        expect:
        twitterStatusRepository.findLastStatus() == status
    }

    def 'getting the last status tweeted is correct when there are multiple statuses that have been tweeted'() {

        setup:
        def status1 = new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: new DateTime(10000))
        def status2 = new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2, tweetedOn: new DateTime(20000))
        testTwitterStatusRepository.save status1
        testTwitterStatusRepository.save status2

        expect:
        twitterStatusRepository.findLastStatus() == status2
    }

    def 'getting the last status tweeted returns null when there are no statuses that have been tweeted'() {

        expect:
        !twitterStatusRepository.findLastStatus()
    }

    def "getting the last status tweeted is correct when there is one status that has and one status that hasn't been tweeted"() {

        setup:
        def status1 = new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: new DateTime())
        def status2 = new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2)
        testTwitterStatusRepository.save status1
        testTwitterStatusRepository.save status2

        expect:
        twitterStatusRepository.findLastStatus() == status1
    }

    def 'updating the tweeted on date of a status is correct'() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test', sequenceNo: 1)

        and:
        def date = new DateTime(100000)

        when:
        twitterStatusRepository.setTweetedOnFor date, 1

        and:
        entityManager.clear()

        then:
        testTwitterStatusRepository.findOne(1).tweetedOn == date
    }

    def "resetting all sets all the twitter statuses' tweeted on date to null"() {

        setup:
        testTwitterStatusRepository.save new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: new DateTime())
        testTwitterStatusRepository.save new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2, tweetedOn: new DateTime())

        when:
        twitterStatusRepository.resetAll()

        and:
        entityManager.clear()

        then:
        !testTwitterStatusRepository.findOne(1).tweetedOn
        !testTwitterStatusRepository.findOne(2).tweetedOn
    }
}