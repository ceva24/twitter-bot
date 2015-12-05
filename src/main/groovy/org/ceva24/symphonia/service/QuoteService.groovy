package org.ceva24.symphonia.service

import groovy.util.logging.Slf4j
import org.ceva24.symphonia.exception.WaitPeriodException
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteService {

    def getNextQuote() {

        log.info 'getting next quote'

        throw new WaitPeriodException(2)
    }
}