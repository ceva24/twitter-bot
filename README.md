# twitter-bot

## Introduction

A bot that tweets status updates.

## Description

*  A bot that tweets a status update from the database each time it receives an HTTP GET request.
*  Tweets occur in sequence as specified in the database.
*  To prevent spamming, there is a configurable quiet period that the bot enters after each tweet. New tweets cannot be sent during the quiet period.
*  Once all status updates have been tweeted, the bot enters a downtime period before resetting and starting again.

## Design

HTTP is used as a trigger as the envisaged deployment platform is a free cloud hosting service that requires a certain level of web activity.

## Contribution

Feel free to fork to create your own Twitter bot.
