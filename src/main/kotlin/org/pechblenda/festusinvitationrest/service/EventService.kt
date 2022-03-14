package org.pechblenda.festusinvitationrest.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.core.context.SecurityContextHolder

import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.festusinvitationrest.repository.IEventRepository
import org.pechblenda.festusinvitationrest.service.`interface`.IEventService
import org.pechblenda.exception.NotFoundException
import org.pechblenda.exception.BadRequestException
import org.pechblenda.festusinvitationrest.entity.Event
import org.pechblenda.festusinvitationrest.entity.User
import org.pechblenda.festusinvitationrest.repository.IUserRepository
import org.pechblenda.service.helper.Validation
import org.pechblenda.service.helper.ValidationType
import org.pechblenda.service.helper.Validations
import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.service.enum.IdType
import org.pechblenda.service.helper.EntityParse
import org.pechblenda.service.helper.ProtectField
import org.pechblenda.service.helper.ProtectFields

import java.util.UUID

@Service
class EventService(
	val eventRepository: IEventRepository,
	val userRepository: IUserRepository,
	val response: Response
): IEventService {

	@Transactional(readOnly = true)
	override fun findAllEventsByUuid(eventUuid: UUID?): ResponseEntity<Any> {
		val authUser = getAuthorizeUser()

		if (eventUuid == null) {
			return response.toListMap(
				eventRepository.findAllByUserUuid(authUser.uuid)
			).ok()
		}

		return response.toMap(
			checkAuthorizeUser(
				authUser.team?.name,
				eventUuid
			)
		).ok()
	}

	@Transactional
	override fun createEvent(request: Request): ResponseEntity<Any> {
		request.validate(Validations(
			Validation(
				"name",
				"El 'name' es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			),
			Validation(
				"urlDataBase",
				"El 'urlDataBase' es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			),
			Validation(
				"endPointInvitation",
				"El 'endPointInvitation' es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			),
			Validation(
				"customTicket",
				"El 'customTicket' es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.BOOLEAN,
				ValidationType.EXIST
			),
			Validation(
				"endDate",
				"El 'endDate' es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		))

		val authUser = getAuthorizeUser()
		val users = userRepository.findAll().toMutableList()
		val event = eventRepository.save(
			request.to<Event>(Event::class)
		)

		users.forEach { user ->
			if (user.team?.name == authUser.team?.name) {
				user.events?.add(event)
			}
		}

		return response.created()
	}

	@Transactional
	override fun updateEvent(eventUuid: UUID, request: Request): ResponseEntity<Any> {
		val authUser = getAuthorizeUser()
		checkAuthorizeUser(authUser.team?.name, eventUuid)

		request["uuid"] = eventUuid.toString()
		request.merge<Event>(
			EntityParse(
				"uuid",
				eventRepository,
				IdType.UUID
			),
			ProtectFields(
				ProtectField("uuid"),
				ProtectField("createDate"),
				ProtectField("user")
			),
			Validations(
				Validation(
					"name",
					"El 'name' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"urlDataBase",
					"El 'urlDataBase' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"endPointInvitation",
					"El 'endPointInvitation' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"customTicket",
					"El 'customTicket' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.BOOLEAN,
					ValidationType.EXIST
				),
				Validation(
					"endDate",
					"El 'endDate' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				)
			)
		)

		return response.ok()
	}

	@Transactional
	override fun deleteEvent(eventUuid: UUID): ResponseEntity<Any> {
		val authUser = getAuthorizeUser()
		val event = checkAuthorizeUser(authUser.team?.name, eventUuid)

		eventRepository.deleteRelation(event.uuid)
		eventRepository.delete(event)

		return response.ok()
	}

	private fun getAuthorizeUser(): User {
		val user = userRepository.findByUserName(
			SecurityContextHolder.getContext().authentication.name
		).orElseThrow {
			UnauthenticatedException("No esta autorizado para realizar esta acción")
		}

		return user
	}

	private fun checkAuthorizeUser(teamName: String?, eventUuid: UUID): Event {
		val event = eventRepository.findById(eventUuid).orElseThrow {
			NotFoundException("No se encuentra el evento")
		}

		val eventAuth = eventRepository.checkTeamNameAndEventUuid(
			teamName,
			eventUuid
		)

		if (!eventAuth) {
			throw UnauthenticatedException("No esta autorizado para realizar esta acción")
		}

		return event
	}

}
