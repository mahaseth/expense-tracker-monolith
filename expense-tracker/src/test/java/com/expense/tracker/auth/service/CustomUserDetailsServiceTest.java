package com.expense.tracker.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService userDetailsService;

	@Test
	void loadUserByUsername_Found_ReturnsDetails() {
		User u = User.builder()
				.id("u1")
				.email("a@b.com")
				.fullName("N")
				.passwordHash("hash")
				.role(UserRole.USER)
				.active(true)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));

		UserDetails d = userDetailsService.loadUserByUsername("a@b.com");

		assertEquals("a@b.com", d.getUsername());
		assertEquals("hash", d.getPassword());
		assertTrue(d.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
	}

	@Test
	void loadUserByUsername_NotFound_Throws() {
		when(userRepository.findByEmail("x@y.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class,
				() -> userDetailsService.loadUserByUsername("x@y.com"));
	}
}
