package org.pechblenda.festusinvitationrest.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping

import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.service.Request
import org.pechblenda.doc.annotation.ApiDocumentation
import org.pechblenda.festusinvitationrest.service.`interface`.IUserService

import javax.servlet.http.HttpServletRequest

@CrossOrigin(methods = [
	RequestMethod.GET,
	RequestMethod.PUT
])
@RestController
@RequestMapping(name = "User", value = ["/rest/users"])
class UserController(
	private val userService: IUserService,
	private val httpExceptionResponse: HttpExceptionResponse
) {

	@GetMapping("/refresh-user-image")
	@ApiDocumentation(path = "doc/user/refresh-user-image.json")
	fun refreshUserImage(servletRequest: HttpServletRequest): ResponseEntity<Any> {
		return userService.refreshUserImage(servletRequest)
	}

	@PutMapping
	@ApiDocumentation(path = "doc/user/update-user.json")
	fun updateUser(@RequestBody request: Request): ResponseEntity<Any> {
		return try {
			userService.updateUser(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

}
