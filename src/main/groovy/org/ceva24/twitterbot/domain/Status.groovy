package org.ceva24.twitterbot.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Status {

    @Id
    Long id

    String text
    Integer sequenceNo
    Date tweetedOn
}