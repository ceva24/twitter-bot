package org.ceva24.symphonia.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.stereotype.Service

@Service
class TwitterService {

    @Autowired
    Twitter twitter

    def sendTweet(String text) {

        return twitter.timelineOperations().updateStatus(text)
    }
}