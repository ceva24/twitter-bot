package org.ceva24.twitterbot.util

import org.joda.time.DateTime
import org.joda.time.Interval

class PeriodCalculator {

    final DateTime current = DateTime.now()
    private final DateTime start
    private final Long periodLength

    PeriodCalculator(DateTime start, Long periodLength) {

        this.start = start
        this.periodLength = periodLength
    }

    def isPeriodActive() {

        return start.plus(durationSinceStart).isBefore(start.plus(periodToMillis()))
    }

    def getDurationSinceStart() {

        return new Interval(start.millis, current.millis).toDuration()
    }

    def getDurationUntilEnd() {

        def end = start.millis + periodToMillis()

        return new Interval(Math.min(current.millis, end), end).toDuration()
    }

    protected def periodToMillis() {

        return periodLength * 1000
    }
}