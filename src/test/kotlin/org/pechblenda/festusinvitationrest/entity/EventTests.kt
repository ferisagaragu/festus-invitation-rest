package org.pechblenda.festusinvitationrest.entity

import java.util.UUID

import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EventTests {

	@Test
	fun `instance event entity`() {
		val uuid = UUID.randomUUID()
		val event = Event(
			uuid = uuid,
			name = "",
			description = "",
			urlDataBase = "",
			endPointInvitation = "",
			primaryColor = "",
			accentColor = "",
			customTicket = false,
			endDate = null,
			createDate = null,
			users = null
		)

		assert(event.uuid == uuid)
		assert(event.name == "")
		assert(event.description == "")
		assert(event.urlDataBase == "")
		assert(event.endPointInvitation == "")
		assert(event.primaryColor == "")
		assert(event.accentColor == "")
		assert(!event.customTicket)
		assert(event.endDate == null)
		assert(event.createDate == null)
		assert(event.users == null)
	}

	@Test
	fun `use event on pre persist method`() {
		val event = Event()
		assert(event.onPrePersist() == Unit)
	}

	@Test
	fun `use event sets`() {
		val uuid = UUID.randomUUID()
		val event = Event()

		event.uuid = uuid
		event.name = "name"
		event.description = "description"
		event.urlDataBase = "urlDataBase"
		event.endPointInvitation = "endPointInvitation"
		event.primaryColor = "primaryColor"
		event.accentColor = "accentColor"
		event.customTicket = true
		event.endDate = null
		event.createDate = null
		event.users = null

		assert(event.uuid == uuid)
		assert(event.name == "name")
		assert(event.description == "description")
		assert(event.urlDataBase == "urlDataBase")
		assert(event.endPointInvitation == "endPointInvitation")
		assert(event.primaryColor == "primaryColor")
		assert(event.accentColor == "accentColor")
		assert(event.customTicket)
		assert(event.endDate == null)
		assert(event.createDate == null)
		assert(event.users == null)
	}

}