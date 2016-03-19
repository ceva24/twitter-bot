package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.TwitterStatus
import org.joda.time.DateTime
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
class TwitterStatusRepositoryIntegrationSpec extends Specification {

    @Autowired
    TwitterStatusRepository twitterStatusRepository

    @Autowired
    EntityManager entityManager

    def "resetting all sets all the twitter statuses' tweeted on date to null"() {

        setup:
        twitterStatusRepository.save new TwitterStatus(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: new DateTime())
        twitterStatusRepository.save new TwitterStatus(id: 2, text: 'test 2', sequenceNo: 2, tweetedOn: new DateTime())

        when:
        twitterStatusRepository.resetAll()

        and:
        entityManager.clear()

        then:
        !twitterStatusRepository.findOne(1L).tweetedOn
        !twitterStatusRepository.findOne(2L).tweetedOn
    }
}