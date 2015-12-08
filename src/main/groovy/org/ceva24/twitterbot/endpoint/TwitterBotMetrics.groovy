package org.ceva24.twitterbot.endpoint

import org.ceva24.twitterbot.repository.StatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.PublicMetrics
import org.springframework.boot.actuate.metrics.Metric
import org.springframework.stereotype.Component

@Component
class TwitterBotMetrics implements PublicMetrics {

    @Autowired
    StatusRepository statusRepository

    @Override
    Collection<Metric<? extends Long>> metrics() {

        def sent = statusRepository.countByTweetedOnIsNotNull()
        def unsent = statusRepository.countByTweetedOnIsNull()

        return [
                new Metric<Long>('twitter.status.sent', sent),
                new Metric<Long>('twitter.status.unsent', unsent),
                new Metric<Long>('twitter.status.total', sent + unsent)
        ] as Collection<Metric<Long>>
    }
}