package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Status
import org.ceva24.twitterbot.repository.StatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TwitterBotService {

    @Autowired
    StatusRepository statusRepository

    @Autowired
    TwitterService twitterService

    @Transactional
    def tweet() {

        def status = statusRepository.findNextStatus()

        updateStatusTweetedOn status

        return twitterService.sendTweet(status)

        // TODO if all statuses tweeted set downtime period active and set all tweetedOn dates to null
    }

    def getLastTweetTime() {

        return statusRepository.findLastStatus()?.tweetedOn?.time
    }

    protected def updateStatusTweetedOn(Status status) {

        status.tweetedOn = new Date()
        statusRepository.save status
    }
}