package org.ceva24.symphonia.controller

import org.ceva24.symphonia.exception.WaitPeriodException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.social.DuplicateStatusException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class SymphoniaControllerAdvice {

    @Value('${org.ceva24.symphonia.quote.wait-period.seconds}')
    Integer waitPeriod

    @ExceptionHandler(WaitPeriodException)
    def inWaitPeriod(HttpServletResponse response, WaitPeriodException e) {

        // TODO message source
        def message = "Cannot tweet quote before minimum wait period of ${waitPeriod} seconds has elapsed (time remaining = ${e.hoursRemaining} seconds)"

        response.sendError HttpStatus.BAD_REQUEST.value(), message
    }

    @ExceptionHandler(DuplicateStatusException)
    def duplicateTweet(HttpServletResponse response, DuplicateStatusException e) {

        response.sendError HttpStatus.BAD_REQUEST.value(), e.message
    }
}