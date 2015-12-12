package org.ceva24.twitterbot.controller

import org.ceva24.twitterbot.exception.DowntimePeriodException
import org.ceva24.twitterbot.exception.QuietPeriodException
import org.joda.time.Period
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.social.DuplicateStatusException
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

class TwitterBotControllerAdviceSpec extends Specification {

    TwitterBotControllerAdvice advice

    def setup() {

        advice = new TwitterBotControllerAdvice(messageSource: Mock(MessageSource))
    }

    def 'quiet period exceptions look up the message content and return http bad request'() {

        given:
        def response = Mock HttpServletResponse

        when:
        advice.inQuietPeriod response, new QuietPeriodException(new Period())

        then:
        1 * advice.messageSource.getMessage('org.ceva24.twitter-bot.in-quiet-period', _, _) >> 'test'
        1 * response.sendError(HttpStatus.BAD_REQUEST.value(), 'test')
    }

    def 'downtime period exceptions look up the message content and return http bad request'() {

        given:
        def response = Mock HttpServletResponse

        when:
        advice.inDowntimePeriod response, new DowntimePeriodException(new Period())

        then:
        1 * advice.messageSource.getMessage('org.ceva24.twitter-bot.in-downtime-period', _, _) >> 'test'
        1 * response.sendError(HttpStatus.BAD_REQUEST.value(), 'test')
    }

    def 'duplicate status exceptions return http bad request'() {

        given:
        def response = Mock HttpServletResponse

        when:
        advice.duplicateTweet response, new DuplicateStatusException('twitter', 'test')

        then:
        1 * response.sendError(HttpStatus.BAD_REQUEST.value(), 'test')
    }
}