package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Tweet
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
class TweetRepositoryIntegrationSpec extends Specification {

    @Autowired
    TweetRepository tweetRepository

    @Autowired
    EntityManager entityManager

    def "resetting all sets all the tweets' tweeted on date to null"() {

        setup:
        tweetRepository.save([new Tweet(id: 1, text: 'test 1', sequenceNo: 1, tweetedOn: DateTime.now()),
                              new Tweet(id: 2, text: 'test 2', sequenceNo: 2, tweetedOn: DateTime.now())])

        when:
        tweetRepository.resetAll()

        and:
        entityManager.clear()

        then:
        !tweetRepository.findOne(1L).tweetedOn
        !tweetRepository.findOne(2L).tweetedOn
    }
}