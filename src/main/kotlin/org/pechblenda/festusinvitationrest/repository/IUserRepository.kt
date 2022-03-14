package org.pechblenda.festusinvitationrest.repository

import org.pechblenda.festusinvitationrest.entity.User

import org.springframework.data.jpa.repository.JpaRepository

import java.util.UUID
import java.util.Optional

interface IUserRepository: JpaRepository<User, UUID> {
	fun findByUserName(userName: String): Optional<User>
}
