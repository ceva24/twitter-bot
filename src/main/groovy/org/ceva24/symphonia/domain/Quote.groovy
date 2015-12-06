package org.ceva24.symphonia.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Quote {

    @Id
    Long id

    String text
    Integer sequenceNo
    Boolean isTweeted
}