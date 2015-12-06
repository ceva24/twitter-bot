package org.ceva24.symphonia.service

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.stereotype.Service

@Slf4j
@Service
class TwitterService {

    @Autowired
    Twitter twitter

    def sendTweet(String text) {

        def tweet = twitter.timelineOperations().updateStatus text

        return tweet
    }
}