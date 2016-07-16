package uk.co.ceva24.twitterbot.domain

import org.hibernate.annotations.Type
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

    @Type(type = 'org.jadira.usertype.dateandtime.joda.PersistentDateTime')
    DateTime activeOn

    static enum ApplicationStatusId { DOWNTIME }
}