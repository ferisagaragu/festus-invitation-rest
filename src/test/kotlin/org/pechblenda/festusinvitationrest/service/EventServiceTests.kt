package org.pechblenda.festusinvitationrest.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.pechblenda.exception.NotFoundException
import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.festusinvitationrest.entity.Event
import org.pechblenda.festusinvitationrest.entity.Team
import org.pechblenda.festusinvitationrest.repository.IEventRepository
import org.pechblenda.festusinvitationrest.repository.ITeamRepository
import org.pechblenda.festusinvitationrest.repository.IUserRepository
import org.pechblenda.service.Request
import org.pechblenda.festusinvitationrest.entity.User

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

import java.util.UUID

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = ["/import-event-service.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventServiceTests {

	@Autowired
	lateinit var eventService: EventService

	@Autowired
	private lateinit var eventRepository: IEventRepository

	@Autowired
	private lateinit var userRepository: IUserRepository

	@Autowired
	private lateinit var teamRepository: ITeamRepository

	private var init = true
	private var restore = false
	private var eventMount: Event? = null
	private var userMount: User? = null
	private var teamMount: Team? = null

	@BeforeEach
	fun beforeEach() {
		restoreEvent()
	}

	fun restoreEvent() {
		if (init) {
			userMount = userRepository.findById(UUID.fromString("0e41ba83-6bc3-4655-b573-25fda614b988")).get()
			teamMount = teamRepository.findById(UUID.fromString("1b09dd22-9c89-4535-b579-b0956fd95632")).get()
			eventMount = eventRepository.findById(UUID.fromString("501e6f70-1c87-46ea-9c6f-2f38e4333e1c")).get()
			init = false
		}

		if (restore) {
			userMount?.team = teamMount
			userRepository.save(userMount!!)
			restore = false
		}
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when return all events from user logged`() {
		val response = eventService.findAllEventsByUuid(null)

		assertEquals(response.statusCodeValue, 200)
		assertEquals(
			((response.body as MutableMap<String, Any>)["data"] as MutableList<Any>).size,
			3
		)
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when return a event from UUID`() {
		val response = eventService.findAllEventsByUuid(eventMount?.uuid)

		assertEquals(response.statusCodeValue, 200)
		assertEquals(
			((response.body as MutableMap<String, Any>)["data"] as MutableMap<String,Any>)["firstCoupleName"],
			eventMount?.firstCoupleName
		)
	}

	@Test
	@WithMockUser(username = "userMockBad", password = "pwd", roles = [])
	fun `throw user not exist`() {
		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.findAllEventsByUuid(eventMount?.uuid)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `throw event not found`() {
		val message = Assertions.assertThrows(NotFoundException::class.java) {
			eventService.findAllEventsByUuid(UUID.randomUUID())
		}.message

		assertEquals(message, "404 NOT_FOUND \"No se encuentra el evento\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when user logged don't have team`() {
		restore = true
		userMount?.team = null
		userRepository.save(userMount!!)

		val message = Assertions.assertThrows(NotFoundException::class.java) {
			eventService.findAllEventsByUuid(UUID.randomUUID())
		}.message

		assertEquals(message, "404 NOT_FOUND \"No se encuentra el evento\"")
	}

	@Test
	@WithMockUser(username = "userMockNotTeam", password = "pwd", roles = [])
	fun `when user logged don't have a correct team`() {
		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.findAllEventsByUuid(eventMount?.uuid)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when create event`() {
		val request = Request()
		request["firstCoupleName"] = "Dony"
		request["secondCoupleName"] = "Shanon"
		request["primaryColor"] = "#FFF"
		request["secondaryColor"] = "#000"
		request["customTicket"] = false
		request["endDate"] = "2022-04-10"
		request["eventDate"] = "2022-06-10"

		val response = eventService.createEvent(request)

		assertEquals(response.statusCodeValue, 201)
	}

	@Test
	@WithMockUser(username = "userMockBad", password = "pwd", roles = [])
	fun `throw event bad request for unauthenticated user`() {
		val request = Request()
		request["firstCoupleName"] = "Dony"
		request["secondCoupleName"] = "Shanon"
		request["primaryColor"] = "#FFF"
		request["secondaryColor"] = "#000"
		request["customTicket"] = false
		request["endDate"] = "2022-04-10"
		request["eventDate"] = "2022-06-10"

		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.createEvent(request)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMockNotTeam", password = "pwd", roles = [])
	fun `when event is create without team on the user`() {
		val request = Request()
		request["firstCoupleName"] = "Dony"
		request["secondCoupleName"] = "Thestaverguer"
		request["primaryColor"] = "#FFF"
		request["secondaryColor"] = "#000"
		request["customTicket"] = false
		request["endDate"] = "2022-04-10"
		request["eventDate"] = "2022-06-10"

		val response = eventService.createEvent(request)

		assertEquals(response.statusCodeValue, 201)
	}

	@Test
	@WithMockUser(username = "userMockNotEvents", password = "pwd", roles = [])
	fun `when event is create and the user didn't have events`() {
		val user = userRepository.findByUserName("userMockNotEvents").get()
		user.events = null
		val request = Request()
		request["firstCoupleName"] = "Dony"
		request["secondCoupleName"] = "Shanon"
		request["primaryColor"] = "#FFF"
		request["secondaryColor"] = "#000"
		request["customTicket"] = false
		request["endDate"] = "2022-04-10"
		request["eventDate"] = "2022-06-10"

		val response = eventService.createEvent(request)

		assertEquals(response.statusCodeValue, 201)
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when event is update`() {
		val request = Request()
		request["firstCoupleName"] = "Dony"
		request["secondCoupleName"] = "Verguer"
		request["primaryColor"] = "#FFF"
		request["secondaryColor"] = "#000"
		request["customTicket"] = true
		request["endDate"] = "2022-04-10"
		request["eventDate"] = "2022-06-10"

		val response = eventService.updateEvent(
			UUID.fromString("501e6f70-1c87-46ea-9c6f-2f38e4333e1c"),
			request
		)

		assertEquals(response.statusCodeValue, 200)
	}

	@Test
	@WithMockUser(username = "userMockNotTeam", password = "pwd", roles = [])
	fun `when event is update but the user don't have team`() {
		val request = Request()
		request["name"] = "Fake event request"
		request["urlDataBase"] = "none"
		request["endPointInvitation"] = "none"
		request["customTicket"] = false
		request["endDate"] = "2022-02-14"

		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.updateEvent(
				UUID.fromString("501e6f70-1c87-46ea-9c6f-2f38e4333e1c"),
				request
			)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when event is delete`() {
		val response = eventService.deleteEvent(
			UUID.fromString("556f6474-7a52-40ec-be52-07a1ae4309dc"),
		)

		assertEquals(response.statusCodeValue, 200)
	}

	@Test
	@WithMockUser(username = "userMockNotTeam", password = "pwd", roles = [])
	fun `when event is delete and user don't have team`() {
		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.deleteEvent(
				UUID.fromString("556f6474-7a52-40ec-be52-07a1ae4309dc"),
			)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `when call generate chart sale`() {
		val response = eventService.generateChartSale()
		assertEquals(response.statusCodeValue, 200)
	}

}