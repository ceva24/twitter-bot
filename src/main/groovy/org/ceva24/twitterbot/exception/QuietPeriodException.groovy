package org.ceva24.twitterbot.exception

import org.joda.time.Period

class QuietPeriodException extends RuntimeException {

    Period timeRemaining

    QuietPeriodException(Period timeRemaining) {

        super('Cannot tweet during quiet period')

        this.timeRemaining = timeRemaining
    }
}