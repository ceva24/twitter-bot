package org.ceva24.symphonia.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate

@ConfigurationProperties(prefix = 'twitter')
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
}