package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.Application
import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.test.TestApplication
import org.ceva24.twitterbot.test.TestConfigRepository
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager

@IntegrationTest
@Transactional
@ActiveProfiles('test')
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [Application, TestApplication])
class ConfigRepositoryIntegrationSpec extends Specification {

    @Autowired
    ConfigRepository configRepository

    @Autowired
    TestConfigRepository testConfigRepository

    @Autowired
    EntityManager entityManager

    def 'updating the downtime period last active date is correct'() {

        setup:
        testConfigRepository.save new Config(id: Config.ConfigId.DOWNTIME)

        and:
        def date = new DateTime()

        when:
        configRepository.setActiveOnFor date, Config.ConfigId.DOWNTIME

        and:
        entityManager.clear()

        then:
        configRepository.findOne(Config.ConfigId.DOWNTIME).activeOn == date
    }
}