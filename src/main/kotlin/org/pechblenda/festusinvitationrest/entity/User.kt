package org.pechblenda.festusinvitationrest.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

import org.pechblenda.auth.entity.IUser
import org.pechblenda.auth.enums.AccountType

import java.util.UUID

@Entity
@Table(name = "users")
class User(
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	override var uuid: UUID,
	override var name: String,
	override var surname: String,
	override var motherSurname: String,
	override var password: String,
	override var photo: String,
	override var activatePassword: UUID?,
	override var accountType: String,

	@Column(unique = true)
	override var userName: String,

	@Column(unique = true)
	override var email: String,

	@Column(columnDefinition = "boolean default false")
	override var enabled: Boolean,

	@Column(columnDefinition = "boolean default false")
	override var active: Boolean
): IUser {

	constructor(): this(
		uuid = UUID.randomUUID(),
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
		active = false
	)

}
