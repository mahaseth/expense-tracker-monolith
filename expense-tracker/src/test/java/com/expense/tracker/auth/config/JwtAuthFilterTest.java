package com.expense.tracker.auth.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.expense.tracker.auth.service.CustomUserDetailsService;
import com.expense.tracker.auth.service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private CustomUserDetailsService userDetailsService;

	private JwtAuthFilter filter;

	@BeforeEach
	void setUp() {
		filter = new JwtAuthFilter(jwtService, userDetailsService);
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilterInternal_NoAuthorization_ContinuesWithoutAuth() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilterInternal(request, response, chain);

		verify(jwtService, never()).isTokenValid(anyString());
		assertNotNull(chain.getRequest());
	}

	@Test
	void doFilterInternal_InvalidToken_ContinuesWithoutSettingAuth() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer bad");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		when(jwtService.isTokenValid("bad")).thenReturn(false);

		filter.doFilterInternal(request, response, chain);

		assertNotNull(chain.getRequest());
	}

	@Test
	void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer good-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		when(jwtService.isTokenValid("good-token")).thenReturn(true);
		when(jwtService.extractEmail("good-token")).thenReturn("user@example.com");

		UserDetails ud = User.withUsername("user@example.com")
				.password("p")
				.roles("USER")
				.build();
		when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(ud);

		filter.doFilterInternal(request, response, chain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("user@example.com",
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
}
