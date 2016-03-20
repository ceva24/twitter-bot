package org.ceva24.twitterbot.service

import groovy.util.logging.Slf4j

@Slf4j
class StubTwitterService extends TwitterService {

    @Override
    def sendTweet(String text) {

        log.info 'Development profile: returning without updating status on twitter'
    }
}