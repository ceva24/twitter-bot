package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import spock.lang.Specification

class TwitterStatusServiceSpec extends Specification {

    TwitterStatusService twitterStatusService

    def setup() {

        twitterStatusService = new TwitterStatusService(twitterStatusRepository: Mock(TwitterStatusRepository))
    }

    def 'getting the last tweet searches the repository and returns the last status'() {

        given:
        def status = new TwitterStatus()

        when:
        def last = twitterStatusService.lastTweet

        then:
        1 * twitterStatusService.twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc() >> status

        and:
        last == status
    }

    def 'all twitter statuses tweeted is true when the repository count of untweeted statuses is 0'() {

        given:
        twitterStatusService.twitterStatusRepository.countByTweetedOnIsNull() >> 0L

        expect:
        twitterStatusService.allStatusesTweeted()
    }

    def 'all twitter statuses tweeted is true when the repository count of untweeted statuses is greater than 0'() {

        given:
        twitterStatusService.twitterStatusRepository.countByTweetedOnIsNull() >> 1L

        expect:
        !twitterStatusService.allStatusesTweeted()
    }

    def 'resetting all twitter statuses resets all statuses in the database'() {

        when:
        twitterStatusService.resetAllTwitterStatuses()

        then:
        1 * twitterStatusService.twitterStatusRepository.resetAll()
    }
}