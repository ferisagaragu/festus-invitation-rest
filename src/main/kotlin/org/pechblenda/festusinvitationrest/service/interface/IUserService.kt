package org.pechblenda.festusinvitationrest.service.`interface`

import javax.servlet.http.HttpServletRequest

import org.springframework.http.ResponseEntity

import org.pechblenda.service.Request

interface IUserService {
	fun refreshUserImage(servletRequest: HttpServletRequest): ResponseEntity<Any>
	fun updateUser(request: Request): ResponseEntity<Any>
}
