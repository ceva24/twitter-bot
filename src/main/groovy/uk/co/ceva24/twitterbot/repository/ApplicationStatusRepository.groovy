package uk.co.ceva24.twitterbot.repository

import uk.co.ceva24.twitterbot.domain.ApplicationStatus
import uk.co.ceva24.twitterbot.domain.ApplicationStatus.ApplicationStatusId
import org.springframework.data.repository.CrudRepository

interface ApplicationStatusRepository extends CrudRepository<ApplicationStatus, ApplicationStatusId> {}