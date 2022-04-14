package org.pechblenda.festusinvitationrest.security

import org.pechblenda.auth.AuthController
import org.pechblenda.auth.service.AuthService
import org.pechblenda.auth.util.ContextApp
import org.pechblenda.festusinvitationrest.entity.User
import org.pechblenda.security.JwtAuthEntryPoint
import org.pechblenda.security.JwtAuthTokenFilter
import org.pechblenda.security.JwtProvider

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

	@Autowired
	private lateinit var userDetailsService: UserDetailsServiceImpl

	@Autowired
	private lateinit var jwtAuthEntryPoint: JwtAuthEntryPoint

	@Autowired
	private lateinit var jwtProvider: JwtProvider

	@Autowired
	private lateinit var authRepository: IAuthRepository

	override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
		authenticationManagerBuilder
			.userDetailsService<UserDetailsService?>(userDetailsService)
			.passwordEncoder(passwordEncoder())
	}

	override fun configure(http: HttpSecurity) {
		http
			.cors()
			.and()
			.csrf()
			.disable()
			.authorizeRequests()
			.antMatchers(
				"/api/**/",
				"/rest/auth/**"
			).permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthEntryPoint)
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

		http.addFilterBefore(
			authenticationJwtTokenFilter(),
			UsernamePasswordAuthenticationFilter::class.java
		)
	}

	@Bean
	fun jwtAuthEntryPoint(): JwtAuthEntryPoint {
		return JwtAuthEntryPoint()
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	@Bean
	fun authenticationJwtTokenFilter(): JwtAuthTokenFilter {
		return JwtAuthTokenFilter(jwtProvider, userDetailsService)
	}

	@Bean
	override fun authenticationManagerBean(): AuthenticationManager {
		return super.authenticationManagerBean()
	}

	@Bean
	fun authService(userRepository: IAuthRepository): AuthService {
		return AuthService(userRepository, User::class)
	}

	@Bean
	fun authController(): AuthController {
		return AuthController()
	}

	@Bean
	fun contextApp(): ContextApp {
		return ContextApp(authRepository)
	}

}
