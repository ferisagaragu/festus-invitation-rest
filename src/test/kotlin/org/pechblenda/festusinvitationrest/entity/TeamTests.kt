package org.pechblenda.festusinvitationrest.entity

import java.util.UUID

import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TeamTests {

	@Test
	fun `instance team entity`() {
		val uuid = UUID.randomUUID()
		val team = Team(
			uuid = uuid,
			name = "",
			createDate = null,
			user = null
		)

		assert(team.uuid == uuid)
		assert(team.name == "")
		assert(team.createDate == null)
		assert(team.user == null)
	}

	@Test
	fun `use team on pre persist method`() {
		val team = Team()
		assert(team.onPrePersist() == Unit)
	}

	@Test
	fun `use team sets`() {
		val uuid = UUID.randomUUID()
		val team = Team()

		team.uuid = uuid
		team.name = "name"
		team.createDate = null
		team.user = null

		assert(team.uuid == uuid)
		assert(team.name == "name")
		assert(team.createDate == null)
		assert(team.user == null)
	}

}