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
			firstCoupleName = "",
			secondCoupleName = "",
			description = "",
			providerUrl = "",
			providerType = "",
			endPointInvitation = "",
			primaryColor = "",
			secondaryColor = "",
			customTicket = false,
			price = 0.0,
			type = "",
			advance = "",
			remaining = "",
			endDate = null,
			eventDate = null,
			createDate = null,
			delete = false,
			users = null
		)

		assert(event.uuid == uuid)
		assert(event.firstCoupleName == "")
		assert(event.secondCoupleName == "")
		assert(event.description == "")
		assert(event.providerUrl == "")
		assert(event.providerType == "")
		assert(event.endPointInvitation == "")
		assert(event.primaryColor == "")
		assert(event.secondaryColor == "")
		assert(!event.customTicket)
		assert(event.price == 0.0)
		assert(event.endDate == null)
		assert(event.eventDate == null)
		assert(event.createDate == null)
		assert(!event.delete)
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
		event.firstCoupleName = "name"
		event.secondCoupleName = "name2"
		event.description = "description"
		event.providerUrl = "urlDataBase"
		event.endPointInvitation = "endPointInvitation"
		event.providerType = "firebase"
		event.primaryColor = "primaryColor"
		event.secondaryColor = "accentColor"
		event.customTicket = true
		event.endDate = null
		event.eventDate = null
		event.createDate = null
		event.delete = false
		event.users = null

		assert(event.uuid == uuid)
		assert(event.firstCoupleName == "name")
		assert(event.secondCoupleName == "name2")
		assert(event.description == "description")
		assert(event.providerUrl == "urlDataBase")
		assert(event.providerType == "firebase")
		assert(event.endPointInvitation == "endPointInvitation")
		assert(event.primaryColor == "primaryColor")
		assert(event.secondaryColor == "accentColor")
		assert(event.customTicket)
		assert(event.endDate == null)
		assert(event.eventDate == null)
		assert(event.createDate == null)
		assert(!event.delete)
		assert(event.users == null)
	}

}