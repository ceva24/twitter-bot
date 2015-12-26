package org.ceva24.twitterbot.test

import org.ceva24.twitterbot.domain.TwitterStatus
import org.springframework.data.repository.Repository

interface TestTwitterStatusRepository extends Repository<TwitterStatus, Long> {

    TwitterStatus findOne(Long id)

    void save(TwitterStatus twitterStatus)

    void deleteAll()
}