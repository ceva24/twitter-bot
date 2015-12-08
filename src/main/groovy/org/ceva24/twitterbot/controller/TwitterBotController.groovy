package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.command.TweetCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class TwitterBotController {

    @Autowired
    AutowireCapableBeanFactory autowireCapableBeanFactory

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def tweet() {

        def command = autowireCapableBeanFactory.createBean TweetCommand

        def tweetResult = command.sendNextTweet()

        return [timestamp: tweetResult.timestamp, status: HttpStatus.OK.value(), id: tweetResult.id, text: tweetResult.text]
    }
}