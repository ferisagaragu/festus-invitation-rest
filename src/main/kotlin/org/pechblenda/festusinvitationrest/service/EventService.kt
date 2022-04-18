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
import java.util.Arrays

import kotlin.streams.toList

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

	override fun generateChartSale(): ResponseEntity<Any> {
		val out = mutableListOf<MutableMap<String, Any>>()

		val years = Arrays.stream((
			eventRepository.findAllCreateDate() +
			eventRepository.findAllEndDate()
		).toIntArray()).distinct().toList()

		years.forEach { year ->
			val element = mutableMapOf<String, Any>()
			val data = mutableListOf<Double>()

			for (i in 1..12) {
				val monthYear = "${if (i <= 9) "0" else ""}$i/$year"
				var priceTotal = 0.0

				eventRepository.findAdvanceByMonthYear(monthYear).forEach { price ->
					priceTotal += if(price.isNotEmpty()) price
						.replace("$", "")
						.replace(",", "")
						.replace(" MNX", "")
						.toDouble() else 0.0
				}

				eventRepository.findRemainingByMonthYear(monthYear).forEach { price ->
					priceTotal += if(price.isNotEmpty()) price
						.replace("$", "")
						.replace(",", "")
						.replace(" MNX", "")
						.toDouble() else 0.0
				}

				data.add(priceTotal)
			}

			element["label"] = year
			element["data"] = data
			out.add(element)
		}

		return response.ok(out)
	}

	@Transactional
	override fun createEvent(request: Request): ResponseEntity<Any> {
		request.validate(getValidations(false))
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
				ProtectField("endDate"),
				ProtectField("eventDate"),
				ProtectField("user")
			),
			getValidations(true)
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

	private fun getValidations(update: Boolean): Validations {
		val validations = arrayListOf(
			"firstCoupleName",
			"secondCoupleName",
			"primaryColor",
			"secondaryColor",
			"customTicket",
			"endDate",
			"eventDate"
		).mapNotNull { validation ->
			if (update) {
				if ((validation != "endDate") && (validation != "eventDate")) {
					Validation(
						validation,
						"El '$validation' es requerido",
						ValidationType.NOT_NULL,
						ValidationType.NOT_BLANK,
						ValidationType.EXIST
					)
				} else {
					null
				}
			} else {
				Validation(
					validation,
					"El '$validation' es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				)
			}
		}

		return Validations(*validations.toTypedArray())
	}

}
