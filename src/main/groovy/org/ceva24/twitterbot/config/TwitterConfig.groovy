package org.ceva24.twitterbot.config

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.domain.Status
import org.ceva24.twitterbot.service.TwitterService
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
    TwitterService twitterService() {

        return new DevTwitterService()
    }

    @Slf4j
    private static class DevTwitterService extends TwitterService {

        String lastStatusText

        def sendTweet(Status status) {

            if (status.text == lastStatusText) throw new DuplicateStatusException('twitter', 'Status is a duplicate.')

            lastStatusText = status.text

            log.info 'development mode enabled: returning without updating status on twitter'

            return new TweetResult(timestamp: new Date(), id: 1L, text: status.text)
        }
    }
}