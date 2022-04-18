package org.pechblenda.festusinvitationrest.service

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions

import org.mockito.Mock
import org.pechblenda.exception.BadRequestException
import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.service.Request

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = ["/import-user-service.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTests {

	@Autowired
	lateinit var userService: UserService

	@Mock
	lateinit var httpServletRequest: HttpServletRequest

	@Test
	@WithMockUser(username = "userMockService", password = "pwd", roles = [])
	fun `when call refresh user image`() {
		val response = userService.refreshUserImage(httpServletRequest)
		Assert.assertEquals(response.statusCodeValue, 200)
		assert(
			((response.body as MutableMap<String, Any>)["data"] as String)
				.contains("/rest/auth/generate-profile-image/us")
		)
	}

	@Test
	@WithMockUser(username = "userMockUnauth", password = "pwd", roles = [])
	fun `when call refresh user image but is Unauthorized`() {
		val message = Assertions.assertThrows(UnauthenticatedException::class.java) {
			userService.refreshUserImage(httpServletRequest)
		}.message

		Assertions.assertEquals(message, "401 UNAUTHORIZED \"No estas autorizado para realizar esta acción\"")
	}

	@Test
	@WithMockUser(username = "userMockService", password = "pwd", roles = [])
	fun `when call update user`() {
		val request = Request()
		request["uuid"] = "0e41ba83-6bc3-4655-b573-25fda614b989"
		request["name"] = "Yourname"

		val response = userService.updateUser(request)
		Assert.assertEquals(response.statusCodeValue, 200)
	}

	@Test
	@WithMockUser(username = "userMockUnauth", password = "pwd", roles = [])
	fun `when call update user but is Unauthorized`() {
		val request = Request()
		request["uuid"] = "0e41ba83-6bc3-4655-b573-25fda614b988"
		request["name"] = "Yourname"

		val message = Assertions.assertThrows(BadRequestException::class.java) {
			userService.updateUser(request)
		}.message

		Assertions.assertEquals(
			message,
			"400 BAD_REQUEST \"Value '0e41ba83-6bc3-4655-b573-25fda614b988' not found matches\""
		)
	}

}