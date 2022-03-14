package org.pechblenda.festusinvitationrest.config

import org.pechblenda.auth.AuthController
import org.pechblenda.doc.Documentation
import org.pechblenda.festusinvitationrest.controller.EventController
import org.pechblenda.festusinvitationrest.entity.Event
import org.pechblenda.festusinvitationrest.entity.User

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan("org.pechblenda.bean")
class BeanConfig {

	@Bean
	fun documentation(): Documentation {
		return Documentation(
			mutableListOf(
				User::class,
				Event::class
			),
			AuthController::class,
			EventController::class
		)
	}

}