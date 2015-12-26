package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.Config.ConfigId
import org.joda.time.DateTime
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface ConfigRepository extends Repository<Config, ConfigId> {

    Config findOne(ConfigId id)

    @Modifying
    @Query('update Config c set c.activeOn = :activeOn where c.id = :id')
    void setActiveOnFor(@Param('activeOn') DateTime activeOn, @Param('id') ConfigId id)
}