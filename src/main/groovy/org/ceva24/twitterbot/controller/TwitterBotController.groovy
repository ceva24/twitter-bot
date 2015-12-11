package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.service.QuietPeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class TwitterBotController {

    @Autowired
    QuietPeriodService quietPeriodService

    @Autowired
    TwitterBotService twitterBotService

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def tweet() {

        quietPeriodService.checkCanTweet()

        def tweetResult = twitterBotService.tweet()

        return [timestamp: tweetResult.timestamp, status: HttpStatus.OK.value(), id: tweetResult.id, text: tweetResult.text]
    }
}