package org.ceva24.twitterbot.domain

import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class Config {

    @Id
    @Enumerated(EnumType.STRING)
    ConfigKey id

    DateTime activeOn

    static enum ConfigKey { DOWNTIME }
}