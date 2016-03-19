package org.ceva24.twitterbot.twitter

import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includePackage = false, includeNames = true)
class Tweet {

    Long id
    DateTime tweetedOn
    String text
}