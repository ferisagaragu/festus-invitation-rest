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
@Sql(scripts = ["/import-event-controller.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventControllerTests {

	@Autowired
	private lateinit var mockMvc: MockMvc

	private var token: String? = null

	@BeforeEach
	fun beforeEach() {
		if (token == null) {
			val requestBody = Request()
			requestBody["userName"] = "userMockEventController"
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
	fun `get event by uuid`() {
		val response = mockMvc.perform(
			get("/rest/events/1c8b5f79-0430-4226-b200-d653098be798")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
		  .andReturn()
			.response
			.contentAsString
		val result = Request().toRequest(response)

		assert(result["data"].toString().contains("uuid"))
	}

	@Test
	fun `get all event from user`() {
		val response = mockMvc.perform(
			get("/rest/events")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString
		val result = Request().toRequest(response)

		assert((result["data"] as List<*>).size > 0)
	}

	@Test
	fun `get event guard`() {
		mockMvc.perform(
			get("/rest/events")
		).andDo(print())
			.andExpect(status().isUnauthorized)
	}

	@Test
	fun `get event not found`() {
		mockMvc.perform(
			get("/rest/events/1c8b5f79-0430-4226-b200-d653098be797")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isNotFound)
	}

	@Test
	fun `create event`() {
		val requestBody = Request()
		requestBody["name"] = "Evento de demostración"
		requestBody["description"] = "Descripción de demo"
		requestBody["urlDataBase"] = "nada"
		requestBody["endPointInvitation"] = "nada"
		requestBody["customTicket"] = false
		requestBody["endDate"] = "2022-04-10"

		mockMvc.perform(
			post("/rest/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isCreated)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `create event bad request`() {
		val requestBody = Request()
		requestBody["description"] = "Descripción de demo"
		requestBody["urlDataBase"] = "nada"
		requestBody["endPointInvitation"] = "nada"
		requestBody["customTicket"] = false
		requestBody["endDate"] = "2022-04-10"

		mockMvc.perform(
			post("/rest/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isBadRequest)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `update event`() {
		val requestBody = Request()
		requestBody["name"] = "Fake name"
		requestBody["description"] = "Descripción de demo"
		requestBody["urlDataBase"] = "nada"
		requestBody["endPointInvitation"] = "nada"
		requestBody["customTicket"] = false
		requestBody["endDate"] = "2022-04-10"

		mockMvc.perform(
			put("/rest/events/1c8b5f79-0430-4226-b200-d653098be798")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `update event bad request`() {
		val requestBody = Request()
		requestBody["description"] = "Descripción de demo"
		requestBody["urlDataBase"] = "nada"
		requestBody["endPointInvitation"] = "nada"
		requestBody["customTicket"] = false
		requestBody["endDate"] = "2022-04-10"

		mockMvc.perform(
			put("/rest/events/1c8b5f79-0430-4226-b200-d653098be798")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody.toJSON())
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isBadRequest)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `delete event`() {
		mockMvc.perform(
			delete("/rest/events/b6374b9f-1e3c-4ab3-a7d3-6a0f9ae4eee2")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isOk)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	fun `delete event bad request`() {
		mockMvc.perform(
			delete("/rest/events/b6374b9f-1e3c-4ab3-a7d3-6a0f9ae4eee1")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
		).andDo(print())
			.andExpect(status().isNotFound)
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

}
