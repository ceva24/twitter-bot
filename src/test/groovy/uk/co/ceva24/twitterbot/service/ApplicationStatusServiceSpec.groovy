package uk.co.ceva24.twitterbot.service

import uk.co.ceva24.twitterbot.domain.ApplicationStatus
import uk.co.ceva24.twitterbot.repository.ApplicationStatusRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.Period
import org.joda.time.PeriodType
import spock.lang.Specification

class ApplicationStatusServiceSpec extends Specification {

    ApplicationStatusService applicationStatusService

    def setup() {

        applicationStatusService = new ApplicationStatusService(applicationStatusRepository: Mock(ApplicationStatusRepository), downtimePeriod: 60)
    }

    def 'getting the downtime status finds it in the repository'() {

        given:
        def status = Mock ApplicationStatus

        when:
        def result = applicationStatusService.downtimeStatus

        then:
        1 * applicationStatusService.applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME) >> status

        and:
        result == status
    }

    def 'the downtime period is active when the time since the downtime period began is less than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        applicationStatusService.applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME) >> new ApplicationStatus(activeOn: new DateTime(100000))

        expect:
        applicationStatusService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when the time since the downtime period last began is greater than the downtime period length'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        applicationStatusService.applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME) >> new ApplicationStatus(activeOn: new DateTime(100000))

        expect:
        !applicationStatusService.isDowntimePeriod()
    }

    def 'the downtime period is inactive when it has never been triggered'() {

        expect:
        !applicationStatusService.isDowntimePeriod()
    }

    def 'the downtime period time remaining when the downtime period is active is correct'() {

        setup:
        DateTimeUtils.currentMillisFixed = 159000

        and:
        applicationStatusService.applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME) >> new ApplicationStatus(activeOn: new DateTime(100000))

        expect:
        applicationStatusService.downtimePeriodTimeRemaining == new Period(1000).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period time remaining is 0 when the period is no longer active'() {

        setup:
        DateTimeUtils.currentMillisFixed = 161000

        and:
        applicationStatusService.applicationStatusRepository.findOne(ApplicationStatus.ApplicationStatusId.DOWNTIME) >> new ApplicationStatus(activeOn: new DateTime(100000))

        expect:
        applicationStatusService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }

    def 'the downtime period remaining is 0 when the downtime period has never been triggered'() {

        expect:
        applicationStatusService.downtimePeriodTimeRemaining == new Period(0).normalizedStandard(PeriodType.yearWeekDayTime())
    }
}