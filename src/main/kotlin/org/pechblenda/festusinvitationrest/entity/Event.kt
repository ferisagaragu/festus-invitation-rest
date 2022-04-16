package org.pechblenda.festusinvitationrest.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.PrePersist
import javax.persistence.Table
import javax.persistence.Column

import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.persistence.Lob

import org.pechblenda.service.annotation.Key
import org.pechblenda.service.enum.DefaultValue

@Entity
@Table(name = "events")
class Event(
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	var uuid: UUID,
	var firstCoupleName: String,
	var secondCoupleName: String,

	@Lob
	var description: String,

	var providerUrl: String,
	var providerType: String,
	var endPointInvitation: String,
	var primaryColor: String,
	var secondaryColor: String,
	var customTicket: Boolean,
	var price: Double,
	var type: String,
	var advance: String,
	var remaining: String,
	var endDate: Date?,
	var eventDate: Date?,
	var createDate: Date?,

	@Column(name = "delete_status", columnDefinition = "boolean default false")
	var delete: Boolean,

	@ManyToMany(mappedBy = "events")
	var users: MutableList<User>?
) {

	constructor() : this(
		uuid = UUID.randomUUID(),
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

	@PrePersist
	fun onPrePersist() {
		createDate = Date()
	}

	@Key(name = "name", autoCall = true, defaultNullValue = DefaultValue.TEXT)
	fun generateName(): String {
		return "$firstCoupleName & $secondCoupleName"
	}

	@Key(name = "type", autoCall = true, defaultNullValue = DefaultValue.NULL)
	fun generateTypeArray(): Any {
		return if (type.contains(",")) {
			type.split(",")
		} else {
			arrayListOf(type)
		}
	}

	@Key(name = "remainingDay", autoCall = true, defaultNullValue = DefaultValue.NUMBER)
	fun generateRemainingDay(): Long {
		return TimeUnit.MILLISECONDS.toDays(
			endDate?.time?.minus(createDate?.time!!)!!
		)
	}

	@Key(name = "missingDay", autoCall = true, defaultNullValue = DefaultValue.NUMBER)
	fun generateMissingDay(): Long {
		return TimeUnit.MILLISECONDS.toDays(
			endDate?.time?.minus(Date()?.time!!)!!
		)
	}

	@Key(name = "percentage", autoCall = true, defaultNullValue = DefaultValue.NUMBER)
	fun generatePercentage(): Long {
		return 100 - (generateMissingDay() * 100) / generateRemainingDay()
	}

}
