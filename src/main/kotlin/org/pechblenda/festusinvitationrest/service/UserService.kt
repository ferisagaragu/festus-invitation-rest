package org.pechblenda.festusinvitationrest.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.festusinvitationrest.service.`interface`.IUserService
import org.pechblenda.festusinvitationrest.entity.User
import org.pechblenda.festusinvitationrest.repository.IUserRepository
import org.pechblenda.service.enum.IdType
import org.pechblenda.service.helper.EntityParse
import org.pechblenda.service.helper.ProtectField
import org.pechblenda.service.helper.ProtectFields
import org.pechblenda.auth.util.ContextApp
import org.pechblenda.core.shared.DynamicResources

import javax.servlet.http.HttpServletRequest

@Service
class UserService(
	val response: Response,
	val userRepository: IUserRepository,
	val contextApp: ContextApp,
	val dynamicResources: DynamicResources
): IUserService {

	@Transactional(readOnly = true)
	override fun refreshUserImage(servletRequest: HttpServletRequest): ResponseEntity<Any> {
		val user = contextApp.getAuthorizeUser()

		return response.ok(
			"",
			dynamicResources.getUserImageUrl(servletRequest, user)
		)
	}

	@Transactional
	override fun updateUser(request: Request): ResponseEntity<Any> {
		request.merge<User>(
			EntityParse(
				"uuid",
				userRepository,
				IdType.UUID
			),
			ProtectFields(
				ProtectField("uuid"),
				ProtectField("password"),
				ProtectField("activatePassword"),
				ProtectField("accountType"),
				ProtectField("userName"),
				ProtectField("email"),
				ProtectField("enabled"),
				ProtectField("active"),
				ProtectField("createDate"),
				ProtectField("active"),
				ProtectField("team"),
				ProtectField("events")
			)
		)

		return response.ok()
	}

}
