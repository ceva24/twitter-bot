package org.ceva24.twitterbot.domain

import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class TwitterStatus {

    @Id
    Long id

    String text
    Integer sequenceNo
    DateTime tweetedOn
}