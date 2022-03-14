package org.pechblenda.festusinvitationrest.repository

import org.pechblenda.festusinvitationrest.entity.Event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

import java.util.UUID

interface IEventRepository: JpaRepository<Event, UUID> {

	@Query(
		"select event from Event event " +
		"inner join event.users user where user.uuid = :userUuid"
	)
	fun findAllByUserUuid(userUuid: UUID): MutableList<Event>

	@Query(
		"select (count(event) > 0) from Event event " +
		"inner join event.users user " +
		"inner join user.team team " +
		"where team.name = :teamName and event.uuid = :eventUuid"
	)
	fun checkTeamNameAndEventUuid(
		teamName: String?,
		eventUuid: UUID
	): Boolean

	@Query(
		value = "delete from users_events where events_uuid = ?1",
		nativeQuery = true
	)
	@Modifying
	fun deleteRelation(eventUuid: UUID)

}
