package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.ApplicationStatus
import org.ceva24.twitterbot.repository.ApplicationStatusRepository
import org.ceva24.twitterbot.util.PeriodCalculator
import org.joda.time.Period
import org.joda.time.PeriodType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApplicationStatusService {

    @Value('${org.ceva24.twitter-bot.tweet.downtime-period.seconds}')
    Integer downtimePeriod

    @Autowired
    ApplicationStatusRepository applicationStatusRepository

    def getDowntimeStatus() {

        return applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME)
    }

    def isDowntimePeriod() {

        def start = applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME)?.activeOn

        return (start ? new PeriodCalculator(start, downtimePeriod).isPeriodActive() : false)
    }

    def getDowntimePeriodTimeRemaining() {

        def start = applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME)?.activeOn

        def period = (start ? new PeriodCalculator(start, downtimePeriod).durationUntilEnd.toPeriod() : new Period(0))

        return period.normalizedStandard(PeriodType.yearWeekDayTime())
    }
}