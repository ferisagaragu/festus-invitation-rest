package org.pechblenda.festusinvitationrest.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

import org.pechblenda.service.Request

import javax.transaction.Transactional

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = ["/import-user-controller.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTests {

	@Autowired
	private lateinit var mockMvc: MockMvc

	private var token: String? = null

	@BeforeEach
	fun beforeEach() {
		if (token == null) {
			val requestBody = Request()
			requestBody["userName"] = "userMockEventControllerUser"
			requestBody["password"] = "fernny27"

			val response = mockMvc.perform(
				post("/rest/auth/sign-in")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody.toJSON())
			).andReturn().response.contentAsString
			val data = requestBody.toRequest(response)["data"] as MutableMap<String, String>
			token = (data["session"] as MutableMap<String, String>)["token"]
		}
	}

	@Test
	fun `refresh user image service`() {
		val response = mockMvc.perform(
			get("/rest/users/refresh-user-image")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
		  .andReturn()
			.response
			.contentAsString
		val result = Request().toRequest(response)

		assert(result["data"].toString().contains("/rest/auth/generate-profile-image"))
	}

	@Test
	fun `refresh user image service unauthenticated`() {
		mockMvc.perform(
			get("/rest/users/refresh-user-image")
				.header(HttpHeaders.AUTHORIZATION, "Bearer ${token}10")
		).andDo(print())
			.andExpect(status().isUnauthorized)
	}

	@Test
	fun `update user service`() {
		val requestBody = Request()
		requestBody["uuid"] = "6cd8602c-de1e-4d20-9318-abeac0706c2d"
		requestBody["name"] = "Barry"
		requestBody["surname"] = "Allen"
		requestBody["motherSurname"] = "Garric"
		requestBody["photo"] = "https://host/rest/users/refresh-user-image"

		mockMvc.perform(
			put("/rest/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `update user service bad request`() {
		val requestBody = Request()
		requestBody["uuid"] = "6cd8602c-de1e-4d20-9318-abeac0706c2e"
		requestBody["name"] = "Barry"
		requestBody["surname"] = "Allen"
		requestBody["motherSurname"] = "Garric"
		requestBody["photo"] = "https://host/rest/users/refresh-user-image"

		mockMvc.perform(
			put("/rest/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isBadRequest)
	}

}
