package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.Config.ConfigId
import org.springframework.data.repository.CrudRepository

interface ConfigRepository extends CrudRepository<Config, ConfigId> {}