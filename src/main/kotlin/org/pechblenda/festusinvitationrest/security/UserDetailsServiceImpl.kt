package org.pechblenda.festusinvitationrest.security

import org.pechblenda.festusinvitationrest.entity.User

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
	private val userRepository: IAuthRepository
): UserDetailsService {

	@Transactional(readOnly = true)
	override fun loadUserByUsername(userName: String): UserDetails {
		val user: User = userRepository.findByUserName(userName).orElseThrow {
			UsernameNotFoundException("Upps no se encuentra el usuario")
		} as User
		return UserPrinciple.build(user)
	}

}
