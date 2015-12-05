package org.ceva24.symphonia.service

import groovy.util.logging.Slf4j
import org.ceva24.symphonia.exception.WaitPeriodException
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteService {

    def getNextQuote() {

        throw new WaitPeriodException(2)
    }
}