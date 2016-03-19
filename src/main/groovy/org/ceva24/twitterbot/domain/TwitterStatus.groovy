package org.ceva24.twitterbot.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.Id

@Entity
@EqualsAndHashCode
@ToString(includePackage = false)
class TwitterStatus {

    @Id
    Long id

    String text
    Integer sequenceNo
    DateTime tweetedOn
}