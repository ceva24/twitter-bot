package org.ceva24.twitterbot.config

import org.ceva24.twitterbot.service.StubTwitterService
import org.ceva24.twitterbot.service.TwitterService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate

@ConfigurationProperties(prefix = 'org.ceva24.twitter-bot.twitter')
@Configuration
class TwitterConfig {

    String consumerKey
    String consumerSecret
    String accessToken
    String accessTokenSecret

    @Bean
    Twitter twitter() {

        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret)
    }

    @Profile(['development', 'test'])
    @Bean
    TwitterService twitterService() {

        return new StubTwitterService()
    }
}