package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.service.DowntimePeriodService
import org.ceva24.twitterbot.service.TwitterBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class TwitterBotController {

    @Autowired
    TwitterBotService twitterBotService

    @Autowired
    DowntimePeriodService downtimePeriodService

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def status() {

        return [status: [lastTweet: twitterBotService.lastTweet, downtime: [active: downtimePeriodService.isDowntimePeriod(),
                                                                            remaining: downtimePeriodService.downtimePeriodTimeRemaining.toStandardSeconds().seconds]]]
    }
}