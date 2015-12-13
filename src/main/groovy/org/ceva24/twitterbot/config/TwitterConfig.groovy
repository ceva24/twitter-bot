package org.ceva24.twitterbot.config

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.service.TweetService
import org.joda.time.DateTime
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.social.DuplicateStatusException
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate

@ConfigurationProperties(prefix = 'org.ceva24.twitter-bot.twitter')
@Configuration
class TwitterConfig {

    def consumerKey
    def consumerSecret
    def accessToken
    def accessTokenSecret

    @Bean
    Twitter twitter() {

        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret)
    }

    @Profile('development')
    @Bean
    TweetService tweetService() {

        return new DevTweetService()
    }

    @Slf4j
    private static class DevTweetService extends TweetService {

        String lastStatusText

        def sendTweet(String text) {

            if (text == lastStatusText) throw new DuplicateStatusException('twitter', 'Status is a duplicate.')

            lastStatusText = text

            log.info 'development profile: returning without updating status on twitter'

            return new TweetResult(timestamp: new DateTime(), id: 1, text: text)
        }
    }
}