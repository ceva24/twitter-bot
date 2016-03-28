package uk.co.ceva24.twitterbot.domain

import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class ApplicationStatus {

    @Id
    @Enumerated(EnumType.STRING)
    ApplicationStatusId id

    DateTime activeOn

    static enum ApplicationStatusId { DOWNTIME }
}