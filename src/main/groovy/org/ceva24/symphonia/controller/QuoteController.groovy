package org.ceva24.symphonia.controller

import groovy.util.logging.Slf4j
import org.ceva24.symphonia.exception.WaitPeriodException
import org.ceva24.symphonia.service.QuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@Slf4j
@RestController
class QuoteController {

    @Autowired
    QuoteService quoteService

    @Value('${org.ceva24.symphonia.quote.wait-period.hours}')
    Integer waitPeriod

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def tweetQuote() {

        log.info 'received request'

        quoteService.nextQuote

        return [code: 200, reason: 'OK']
    }

    @ExceptionHandler(WaitPeriodException)
    def inWaitPeriod(HttpServletResponse response, WaitPeriodException e) {

        // TODO message source
        def message = "Cannot tweet quote before minimum wait period of ${waitPeriod} hours has elapsed (time remaining = ${e.hoursRemaining} hours)"

        log.error message, e

        response.sendError(HttpStatus.BAD_REQUEST.value(), message)
    }
}