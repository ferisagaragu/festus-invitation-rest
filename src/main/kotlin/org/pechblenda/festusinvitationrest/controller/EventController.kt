package org.pechblenda.festusinvitationrest.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping

import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.service.Request
import org.pechblenda.doc.annotation.ApiDocumentation
import org.pechblenda.festusinvitationrest.service.`interface`.IEventService

import java.util.UUID

@CrossOrigin(methods = [
	RequestMethod.GET,
	RequestMethod.POST,
	RequestMethod.PUT,
	RequestMethod.DELETE
])
@RestController
@RequestMapping(name = "Event", value = ["/rest/events"])
class EventController(
	private val eventService: IEventService,
	private val httpExceptionResponse: HttpExceptionResponse
) {

	@GetMapping(value = ["/{eventUuid}", ""])
	@ApiDocumentation(path = "doc/event/find-all-events-by-uuid.json")
	fun findAllEventsByUuid(
		@PathVariable eventUuid: UUID?
	): ResponseEntity<Any> {
		return try {
			eventService.findAllEventsByUuid(eventUuid)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping(value = ["/generate-chart-sale"])
	@ApiDocumentation(path = "doc/event/generate-chart-sale.json")
	fun generateChartSale(): ResponseEntity<Any> {
		return try {
			eventService.generateChartSale()
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping
	@ApiDocumentation(path = "doc/event/create-event.json")
	fun createEvent(@RequestBody request: Request): ResponseEntity<Any> {
		return try {
			eventService.createEvent(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PutMapping("/{eventUuid}")
	@ApiDocumentation(path = "doc/event/update-event.json")
	fun updateEvent(
		@PathVariable eventUuid: UUID,
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			eventService.updateEvent(eventUuid, request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@DeleteMapping("/{eventUuid}")
	@ApiDocumentation(path = "doc/event/delete-event.json")
	fun deleteEvent(
		@PathVariable eventUuid: UUID
	): ResponseEntity<Any> {
		return try {
			eventService.deleteEvent(eventUuid)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

}
