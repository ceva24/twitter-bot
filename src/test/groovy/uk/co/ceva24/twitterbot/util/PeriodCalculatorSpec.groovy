package uk.co.ceva24.twitterbot.util

import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class PeriodCalculatorSpec extends Specification {

    PeriodCalculator periodCalculator

    def 'the period is not active when the current time is after the period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(100000), 60)

        expect:
        !periodCalculator.isPeriodActive()
    }

    def 'the period is active when the current time is within the period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(100000), 60)

        expect:
        periodCalculator.isPeriodActive()
    }

    def 'getting the duration since the start of a period is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 140000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(100000), 60)

        expect:
        periodCalculator.durationSinceStart.millis == 40000
    }

    def 'getting the duration since the start of a period when the start and current time are the same is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(100000), 60)

        expect:
        periodCalculator.durationSinceStart.millis == 0
    }

    def 'getting the duration since the start of a period when the current time is before the start throws an exception'() {

        setup:
        DateTimeUtils.currentMillisFixed = 90000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(100000), 60)

        when:
        periodCalculator.durationSinceStart

        then:
        thrown IllegalArgumentException
    }

    def 'getting the duration until the end of a period is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(50000), 60)

        expect:
        periodCalculator.durationUntilEnd.millis == 10000
    }

    def 'getting the duration until the end of a period when the current time is after the end is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(39000), 60)

        expect:
        periodCalculator.durationUntilEnd.millis == 0
    }

    def 'getting the duration until the end of a period when the start time is equal to the current time is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        periodCalculator = new PeriodCalculator(new DateTime(40000), 60)

        expect:
        periodCalculator.durationUntilEnd.millis == 0
    }
}