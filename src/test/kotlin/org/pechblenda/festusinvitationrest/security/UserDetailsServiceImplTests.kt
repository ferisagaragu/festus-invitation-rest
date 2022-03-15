package org.pechblenda.festusinvitationrest.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = ["/import-user-detail-service-impl.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDetailsServiceImplTests {

	@Autowired
	lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

	@Test
	fun `when user load by user name`() {
		userDetailsServiceImpl.loadUserByUsername("userMockDetail")
	}

	@Test
	fun `throw user not found`() {
		val message = Assertions.assertThrows(UsernameNotFoundException::class.java) {
			userDetailsServiceImpl.loadUserByUsername("not-fount")
		}.message

		Assertions.assertEquals(message, "Upps no se encuentra el usuario")
	}

}