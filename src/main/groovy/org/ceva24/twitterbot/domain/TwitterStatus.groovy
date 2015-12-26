package org.ceva24.twitterbot.domain

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.Id

@Entity
@EqualsAndHashCode
class TwitterStatus {

    @Id
    Long id

    String text
    Integer sequenceNo
    DateTime tweetedOn
}