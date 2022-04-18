package org.pechblenda.festusinvitationrest.entity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.UUID

import org.junit.jupiter.api.Assertions
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
		event.type = "firebase"
		event.advance = "$0.00 MNX"
		event.remaining = "$0.00 MNX"
		event.price = 0.0
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
		assert(event.type == "firebase")
		assert(event.advance == "$0.00 MNX")
		assert(event.remaining == "$0.00 MNX")
		assert(event.price == 0.0)
		assert(event.users == null)
	}

	@Test
	fun `call generate name method`() {
		val event = Event()
		event.firstCoupleName = "Ale"
		event.secondCoupleName = "Fer"
		assert(event.generateName() == "Ale & Fer")
	}

	@Test
	fun `call generate type array`() {
		val event = Event()
		event.type = "webPage,pdf"
		assert((event.generateTypeArray() as ArrayList<String>).size == 2)
	}

	@Test
	fun `call generate remaining day and missing day`() {
		val formatter = SimpleDateFormat("yyyy-MM-dd")
		val event = Event()

		formatter.timeZone = TimeZone.getTimeZone("America/Los_Angeles");
		event.endDate = formatter.parse("2022-05-15")
		event.createDate = formatter.parse("2022-04-18")

		assert(event.generateRemainingDay() is Long)
		assert(event.generateMissingDay() is Long)
		assert(event.generatePercentage() is Long)
	}

	@Test
	fun `call generate remaining day and missing day with nulls`() {
		val event = Event()

		val messageRemainingDay = Assertions.assertThrows(NullPointerException::class.java) {
			event.generateRemainingDay()
		}

		event.endDate = Date(1655527495)

		val messageRemainingDayValidation = Assertions.assertThrows(NullPointerException::class.java) {
			event.generateRemainingDay()
		}

		event.endDate = null

		val messageMissingDay = Assertions.assertThrows(NullPointerException::class.java) {
			event.generateMissingDay()
		}

		assert(messageRemainingDay is NullPointerException)
		assert(messageRemainingDayValidation is NullPointerException)
		assert(messageMissingDay is NullPointerException)
	}

}