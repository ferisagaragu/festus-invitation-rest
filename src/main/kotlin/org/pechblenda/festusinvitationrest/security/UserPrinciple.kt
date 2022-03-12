package org.pechblenda.festusinvitationrest.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority

import com.fasterxml.jackson.annotation.JsonIgnore

import org.pechblenda.festusinvitationrest.entity.User

import java.util.UUID

class UserPrinciple(
	val uuid: UUID,
	private val userName: String,

	@JsonIgnore
	private val password: String,

	private val authority: List<SimpleGrantedAuthority>,
	private val enabled: Boolean
) : UserDetails {

	companion object {
		@JvmStatic
		fun build(user: User): UserPrinciple {
			/*val authorities: List<SimpleGrantedAuthority> = user.roles.map {
				role -> SimpleGrantedAuthority(role.name)
			}*/
			val authorities = listOf<SimpleGrantedAuthority>()

			return UserPrinciple(
				user.uuid,
				user.userName,
				user.password,
				authorities,
				user.enabled
			)
		}
	}

	override fun getAuthorities(): List<SimpleGrantedAuthority> {
		return authority
	}

	override fun getUsername(): String {
		return userName
	}

	override fun getPassword(): String {
		return password
	}

	override fun isAccountNonExpired(): Boolean {
		return true
	}

	override fun isAccountNonLocked(): Boolean {
		return true
	}

	override fun isCredentialsNonExpired(): Boolean {
		return true
	}

	override fun isEnabled(): Boolean {
		return enabled
	}

}
