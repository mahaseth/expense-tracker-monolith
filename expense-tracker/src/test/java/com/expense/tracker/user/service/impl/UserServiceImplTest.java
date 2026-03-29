package com.expense.tracker.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.expense.tracker.user.dto.UserMeResponse;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("me@example.com", null));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void getCurrentUser_Found_ReturnsDto() {
		User user = User.builder()
				.id("u1")
				.email("me@example.com")
				.fullName("Me User")
				.passwordHash("x")
				.role(UserRole.USER)
				.active(true)
				.build();
		when(userRepository.findByEmail("me@example.com")).thenReturn(Optional.of(user));

		UserMeResponse r = userService.getCurrentUser();

		assertEquals("u1", r.getId());
		assertEquals("Me User", r.getFullName());
		assertEquals("me@example.com", r.getEmail());
		assertEquals("USER", r.getRole());
	}

	@Test
	void getCurrentUser_NotFound_Throws() {
		when(userRepository.findByEmail("me@example.com")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> userService.getCurrentUser());
	}
}
