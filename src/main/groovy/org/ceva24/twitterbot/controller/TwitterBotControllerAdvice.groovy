package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.exception.QuietPeriodException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.social.DuplicateStatusException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class TwitterBotControllerAdvice {

    @Autowired
    MessageSource messageSource

    @ExceptionHandler(QuietPeriodException)
    def inQuietPeriod(HttpServletResponse response, QuietPeriodException e) {

        def params = [e.timeRemaining.hours, e.timeRemaining.minutes, e.timeRemaining.seconds] as Object[]
        def message = messageSource.getMessage 'org.ceva24.twitter-bot.in-quiet-period', params, Locale.default

        response.sendError HttpStatus.BAD_REQUEST.value(), message
    }

    @ExceptionHandler(DuplicateStatusException)
    def duplicateTweet(HttpServletResponse response, DuplicateStatusException e) {

        response.sendError HttpStatus.BAD_REQUEST.value(), e.message
    }
}