package org.ceva24.twitterbot.exception

import org.joda.time.Period
import org.joda.time.PeriodType

class DowntimePeriodException extends RuntimeException {

    Period timeRemaining

    DowntimePeriodException(Period timeRemaining) {

        super('Cannot tweet during downtime period')

        this.timeRemaining = timeRemaining.normalizedStandard(PeriodType.yearWeekDayTime())
    }
}