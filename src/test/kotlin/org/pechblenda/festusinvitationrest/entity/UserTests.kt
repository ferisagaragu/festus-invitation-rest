package org.pechblenda.festusinvitationrest.entity

import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest

import org.pechblenda.auth.enums.AccountType

import java.util.UUID

@SpringBootTest
class UserTests {

	@Test
	fun `instance user entity`() {
		val uuid = UUID.randomUUID()
		val user = User(
			uuid = uuid,
			name = "",
			surname = "",
			motherSurname = "",
			password = "",
			photo = "",
			activatePassword = null,
			accountType = AccountType.DEFAULT.name,
			userName = "",
			email = "",
			enabled = false,
			active = false,
			createDate = null,
			events = null
		)

		assert(user.uuid == uuid)
		assert(user.name == "")
		assert(user.surname == "")
		assert(user.motherSurname == "")
		assert(user.password == "")
		assert(user.photo == "")
		assert(user.activatePassword == null)
		assert(user.accountType == AccountType.DEFAULT.name)
		assert(user.userName == "")
		assert(user.email == "")
		assert(!user.enabled)
		assert(!user.active)
		assert(user.createDate == null)
		assert(user.events == null)
	}

	@Test
	fun `use user on pre persist method`() {
		val user = User()
		assert(user.onPrePersist() == Unit)
	}

	@Test
	fun `use user sets`() {
		val uuid = UUID.randomUUID()
		val user = User()

		user.uuid = uuid
		user.name = "name"
		user.surname = "surname"
		user.motherSurname = "motherSurname"
		user.password = "password"
		user.photo = "photo"
		user.activatePassword = null
		user.accountType = AccountType.DEFAULT.name
		user.userName = "userName"
		user.email = "email"
		user.enabled = true
		user.active = true
		user.createDate = null
		user.events = null

		assert(user.uuid == uuid)
		assert(user.name == "name")
		assert(user.surname == "surname")
		assert(user.motherSurname == "motherSurname")
		assert(user.password == "password")
		assert(user.photo == "photo")
		assert(user.activatePassword == null)
		assert(user.accountType == AccountType.DEFAULT.name)
		assert(user.userName == "userName")
		assert(user.email == "email")
		assert(user.enabled)
		assert(user.active)
		assert(user.createDate == null)
		assert(user.events == null)
	}

}