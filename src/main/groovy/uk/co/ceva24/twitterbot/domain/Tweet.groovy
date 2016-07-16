package uk.co.ceva24.twitterbot.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.Id

@Entity
@EqualsAndHashCode
@ToString(includePackage = false)
class Tweet {

    @Id
    Long id

    String text
    Integer sequenceNo

    @Type(type = 'org.jadira.usertype.dateandtime.joda.PersistentDateTime')
    DateTime tweetedOn
}