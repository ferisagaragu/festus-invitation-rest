package org.pechblenda.festusinvitationrest.entity

import java.util.Date
import java.util.UUID

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.PrePersist
import javax.persistence.Table

@Entity
@Table(name = "teams")
class Team(
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	var uuid: UUID,
	var name: String,
	var createDate: Date?,

	@OneToMany(mappedBy = "team")
	var user: MutableList<User>?
) {

	constructor() : this(
		uuid = UUID.randomUUID(),
		name = "",
		createDate = null,
		user = null
	)

	@PrePersist
	fun onPrePersist() {
		this.createDate = Date()
	}

}
