package org.ceva24.twitterbot.repository

import org.ceva24.twitterbot.domain.ApplicationStatus
import org.ceva24.twitterbot.domain.ApplicationStatus.ApplicationStatusId
import org.springframework.data.repository.CrudRepository

interface ApplicationStatusRepository extends CrudRepository<ApplicationStatus, ApplicationStatusId> {}