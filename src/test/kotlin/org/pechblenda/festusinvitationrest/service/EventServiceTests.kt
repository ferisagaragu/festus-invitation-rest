package org.pechblenda.festusinvitationrest.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith

import org.pechblenda.festusinvitationrest.repository.IEventRepository
import org.pechblenda.festusinvitationrest.repository.IUserRepository
import org.pechblenda.festusinvitationrest.entity.Event
import org.pechblenda.festusinvitationrest.entity.Team
import org.pechblenda.festusinvitationrest.entity.User
import org.pechblenda.festusinvitationrest.repository.ITeamRepository
import org.pechblenda.exception.NotFoundException
import org.pechblenda.exception.UnauthenticatedException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

import java.util.Date
import java.util.UUID

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceTests {

	@Autowired
	lateinit var eventService: EventService

	@Autowired
	private lateinit var eventRepository: IEventRepository

	@Autowired
	private lateinit var userRepository: IUserRepository

	@Autowired
	private lateinit var teamRepository: ITeamRepository

	private var restore = true
	private var eventMount: Event? = null
	private var userMount: User? = null
	private var teamMount: Team? = null

	@BeforeAll
	fun beforeAll() {
		val user = User()
		user.name = "userMock"
		user.surname = ""
		user.motherSurname = ""
		user.userName = "userMock"
		user.email = "no-real@fake.com"
		user.password = ""
		user.enabled = true
		user.active = true
		user.photo = ""
		userMount = userRepository.save(user)

		val team = Team()
		team.name = "Fake Team"
		teamMount = teamRepository.save(team)

		val event = Event()
		event.name = "Fake Event"
		event.description = "Fake description"
		event.urlDataBase = "none"
		event.endPointInvitation = "none"
		event.primaryColor = ""
		event.accentColor = "#FFF"
		event.customTicket = false
		event.endDate = Date()
		eventMount = eventRepository.save(event)

		val events = mutableListOf<Event>()
		events.add(eventMount!!)
		userMount?.events = events
		userMount?.team = teamMount
		userMount = userRepository.save(userMount!!)
	}

	@BeforeEach
	fun beforeEach() {
		restoreEvent()
	}

	fun restoreEvent() {
		if (restore) {
			userMount?.team = teamMount
			userRepository.save(userMount!!)
		}
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `test to validate service when return all events from user logged`() {
		val response = eventService.findAllEventsByUuid(null)

		assertEquals(response.statusCodeValue, 200)
		assertEquals(
			((response.body as MutableMap<String, Any>)["data"] as MutableList<Any>).size,
			1
		)
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `test to validate service when return a event from UUID`() {
		val response = eventService.findAllEventsByUuid(eventMount?.uuid)

		assertEquals(response.statusCodeValue, 200)
		assertEquals(
			((response.body as MutableMap<String, Any>)["data"] as MutableMap<String,Any>)["name"],
			eventMount?.name
		)
	}

	@Test
	@WithMockUser(username = "userMockBad", password = "pwd", roles = [])
	fun `test to validate service when throw user not exist`() {
		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.findAllEventsByUuid(eventMount?.uuid)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `test to validate service when throw event not found`() {
		val message = Assertions.assertThrows(NotFoundException::class.java) {
			eventService.findAllEventsByUuid(UUID.randomUUID())
		}.message

		assertEquals(message, "404 NOT_FOUND \"No se encuentra el evento\"")
	}

	@Test
	@WithMockUser(username = "userMock", password = "pwd", roles = [])
	fun `test to validate service when user logged don't have team`() {
		userMount?.team = null
		userRepository.save(userMount!!)

		val message = Assertions.assertThrows(NotFoundException::class.java) {
			eventService.findAllEventsByUuid(UUID.randomUUID())
		}.message

		assertEquals(message, "404 NOT_FOUND \"No se encuentra el evento\"")
	}

	@Test
	@WithMockUser(username = "userMockNotTeam", password = "pwd", roles = [])
	fun `test to validate service when user logged don't have a correct team`() {
		val user = User()
		user.name = "userMock"
		user.userName = "userMockNotTeam"
		user.email = "no-realTeam@fake.com"
		user.password = ""
		user.enabled = true
		user.active = true
		userRepository.save(user)

		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			eventService.findAllEventsByUuid(eventMount?.uuid)
		}.message

		assertEquals(message, "401 UNAUTHORIZED \"No esta autorizado para realizar esta acción\"")
	}

/*
	@Test
	fun `test validate user not fount`() {
		userMount!!.userName = "${userMount!!.userName}123"

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			((authService.validateToken().body as Map<String, Any>)["data"]
					as Map<String, Any>)["validateToken"]
		}.message

		assertEquals(message, "400 BAD_REQUEST \"Upps no se encuentra el usuario\"")
	}

	@Test
	fun `test validate account not activate`() {
		userMount!!.active = false

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			((authService.validateToken().body as Map<String, Any>)["data"]
					as Map<String, Any>)["validateToken"]
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta aun no esta activada, " +
					"revisa tu correo electrónico para saber como activarla\""
		)
	}

	@Test
	fun `test validate account blocked`() {
		userMount!!.enabled = false

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			((authService.validateToken().body as Map<String, Any>)["data"]
					as Map<String, Any>)["validateToken"]
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta se encuentra bloqueada, " +
					"te enviamos a tu correo electrónico las razones\""
		)
	}

	@Test
	fun `test validate can activate`() {
		userMount!!.active = false
		var body = authService.canActivate(userMount!!.uid).body as Map<String, Any>
		body = (body["data"] as Map<String, Any>)
		assertEquals(body["canActivate"], true)
	}

	@Test
	fun `test validate account is active`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			authService.canActivate(userMount!!.uid)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta ya esta activada, " +
					"intenta iniciando sesión de forma habitual\""
		)
	}

	@Test
	fun `test validate can activate not fount user`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			authService.canActivate(UUID.randomUUID())
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps no se encuentra el usuario que quieres activar\""
		)
	}

	@Test
	fun `test validate can change password`() {
		userMount!!.activatePassword = UUID.randomUUID()
		var body = authService.canChangePassword(
			userMount!!.activatePassword!!
		).body as Map<String, Any>
		body = (body["data"] as Map<String, Any>)

		assertEquals(body["canChangePassword"], true)
	}

	@Test
	fun `test validate can change password code not fount`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			authService.canChangePassword(UUID.randomUUID())
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps el código de recuperación no es valido\""
		)
	}

	@Test
	fun `test activate account`() {
		userMount!!.active = false

		val request = Request()
		request["uid"] = userMount!!.uid
		request["password"] = "fakeUserPassword"

		var message = (
				authService.activateAccount(
					request
				).body as Map<String, Any>
				)["message"]

		assertEquals(message, "Tu cuenta a sido activada con éxito")
	}

	@Test
	fun `test activate account password not fount`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["password"] = ""
			authService.activateAccount(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps la contraseña es requerida\""
		)
	}

	@Test
	fun `test activate account password user not fount`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["password"] = "asd"
			request["uid"] = UUID.randomUUID()
			authService.activateAccount(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps no se encuentra el usuario\""
		)
	}

	@Test
	fun `test activate account is activated`() {
		userMount!!.active = true

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["password"] = "asd"
			request["uid"] = userMount!!.uid
			authService.activateAccount(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta ya esta activada\""
		)
	}

	@Test
	fun `test change password`() {
		userMount!!.activatePassword = UUID.randomUUID()

		val request = Request()
		request["activatePassword"] = userMount!!.activatePassword
		request["password"] = "fakeUserPassword"

		var message = (
				authService.changePassword(
					request
				).body as Map<String, Any>
				)["message"]

		assertEquals(message, "Has cambiado tu contraseña con éxito")
	}

	@Test
	fun `test change password is void password`() {
		userMount!!.activatePassword = UUID.randomUUID()

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["activatePassword"] = userMount!!.activatePassword
			request["password"] = ""
			authService.changePassword(request)
		}.message

		assertEquals(message, "400 BAD_REQUEST \"Upps la contraseña es requerida\"")
	}

	@Test
	fun `test change password not activate password`() {
		userMount!!.activatePassword = UUID.randomUUID()

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["activatePassword"] = UUID.randomUUID()
			request["password"] = "fake"
			authService.changePassword(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps no se encuentra ningún " +
					"registro que coincida con el correo electrónico\""
		)
	}

	@Test
	fun `test recover password`() {
		val request = Request()
		request["email"] = "no-realImpl@fake.com"

		var message = (
				authService.recoverPassword(
					request
				).body as Map<String, Any>
				)["message"]

		assertEquals(
			message,
			"Hemos enviado envido un correo " +
					"electrónico a no-realImpl@fake.com con " +
					"las instrucciones para recuperar tu contraseña"
		)
	}

	@Test
	fun `test recover password email not fount`() {
		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["email"] = "ferisagaragu@gmail.com"
			authService.recoverPassword(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps no se encuentra ningún " +
					"registro que coincida con el correo electrónico\""
		)
	}

	@Test
	fun `test sign up`() {
		Mockito.doReturn("url-image")
			.`when`(firebaseStorage).put(
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				any<ByteArray>()
			)

		val request = Request()
		request["name"] = "userFake123"
		request["userName"] = "userFake123"
		request["email"] = "userFake123@fake.com"

		var message = (
				authService.signUp(
					request
				).body as Map<String, Any>
				)["message"]

		assertEquals(
			message,
			"Tu cuenta a sido creada con éxito, " +
					"te enviamos un correo electrónico con " +
					"instrucciones de como activarla"
		)
	}

	@Test
	fun `test sign up name not empty`() {
		Mockito.doReturn("url-image")
			.`when`(firebaseStorage).put(
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				any<ByteArray>()
			)

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["name"] = ""
			request["userName"] = "userFake123"
			request["email"] = "userFake123@fake.com"
			authService.signUp(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps el nombre es requerido\""
		)
	}

	@Test
	fun `test sign up user name not empty`() {
		Mockito.doReturn("url-image")
			.`when`(firebaseStorage).put(
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				any<ByteArray>()
			)

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["name"] = "userFake123Name"
			request["userName"] = ""
			request["email"] = "userFake123@fake.com"
			authService.signUp(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps el nombre de usuario es requerido\""
		)
	}

	@Test
	fun `test sign up email not empty`() {
		Mockito.doReturn("url-image")
			.`when`(firebaseStorage).put(
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(),
				any<ByteArray>()
			)

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["name"] = "userFake123Name"
			request["userName"] = "userFake123"
			request["email"] = ""
			authService.signUp(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps el correo electrónico es requerido\""
		)
	}

	@Test
	fun `test sign in`() {
		val request = Request()
		request["userName"] = "no-realImpl@fake.com"
		request["password"] = "fake"

		var email = ((
				authService.signIn(request).body as Map<String, Any>
				)["data"] as Map<String, Any>)["email"]

		assertEquals(email, "no-realImpl@fake.com")
	}

	@Test
	fun `test sign in account not activate`() {
		userMount!!.active = false

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["userName"] = "no-realImpl@fake.com"
			request["password"] = "fake"
			authService.signIn(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta aun no esta activada, " +
					"revisa tu correo electrónico para saber como activarla\""
		)
	}

	@Test
	fun `test sign in account blocked`() {
		userMount!!.enabled = false

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			val request = Request()
			request["userName"] = "no-realImpl@fake.com"
			request["password"] = "fake"
			authService.signIn(request)
		}.message

		assertEquals(
			message,
			"400 BAD_REQUEST \"Upps tu cuenta se encuentra bloqueada, " +
					"te enviamos a tu correo electrónico las razones\""
		)
	}

 */
}