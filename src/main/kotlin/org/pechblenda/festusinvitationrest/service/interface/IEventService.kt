package org.pechblenda.festusinvitationrest.service.`interface`

import org.springframework.http.ResponseEntity

import org.pechblenda.service.Request

import java.util.UUID

interface IEventService {
	fun findAllEventsByUuid(eventUuid: UUID?): ResponseEntity<Any>
	fun generateChartSale(): ResponseEntity<Any>
	fun createEvent(request: Request): ResponseEntity<Any>
	fun updateEvent(eventUuid: UUID, request: Request): ResponseEntity<Any>
	fun deleteEvent(eventUuid: UUID): ResponseEntity<Any>
}
