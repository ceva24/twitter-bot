package org.ceva24.symphonia.controller

import groovy.util.logging.Slf4j
import org.ceva24.symphonia.service.SymphoniaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class SymphoniaController {

    @Autowired
    SymphoniaService symphoniaService

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def tweetQuote() {

        def tweet = symphoniaService.tweetNextQuote()

        return [timestamp: tweet.timestamp, status: HttpStatus.OK.value(), id: tweet.id, text: tweet.text]
    }
}