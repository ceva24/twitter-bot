package org.ceva24.symphonia.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Quote {

    @Id
    Long id

    @Column
    String text

    @Column(unique = true)
    Integer sequenceNo

    @Column
    Boolean isTweeted
}