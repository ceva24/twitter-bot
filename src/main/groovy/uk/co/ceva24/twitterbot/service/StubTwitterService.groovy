package uk.co.ceva24.twitterbot.service

import groovy.util.logging.Slf4j

@Slf4j
class StubTwitterService extends TwitterService {

    @Override
    def sendTweet(String text) {

        log.info 'Returning without sending tweet to Twitter'
    }
}