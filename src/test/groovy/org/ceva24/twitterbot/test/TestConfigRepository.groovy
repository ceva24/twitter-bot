package org.ceva24.twitterbot.test

import org.ceva24.twitterbot.domain.Config
import org.springframework.data.repository.Repository

interface TestConfigRepository extends Repository<Config, Config.ConfigKey> {

    void save(Config config)
}