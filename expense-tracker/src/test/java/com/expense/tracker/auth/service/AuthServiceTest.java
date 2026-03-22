package com.expense.tracker.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.expense.tracker.auth.dto.AuthResponse;
import com.expense.tracker.auth.dto.LoginRequest;
import com.expense.tracker.auth.dto.RegisterRequest;
import com.expense.tracker.auth.service.impl.AuthServiceImpl;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void register_NewUser_ReturnsToken() {
		RegisterRequest request = new RegisterRequest();
		request.setFullName("Test User");
		request.setEmail("test@example.com");
		request.setPassword("password123");

		when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
		when(jwtService.generateToken("test@example.com")).thenReturn("jwt-token");

		User savedUser = User.builder()
				.id("user-1")
				.email("test@example.com")
				.fullName("Test User")
				.passwordHash("hashedPassword")
				.role(UserRole.USER)
				.active(true)
				.build();
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		AuthResponse response = authService.register(request);

		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		assertEquals("Bearer", response.getTokenType());
		verify(userRepository).save(any(User.class));
		verify(jwtService).generateToken("test@example.com");
	}

	@Test
	void register_DuplicateEmail_ThrowsException() {
		RegisterRequest request = new RegisterRequest();
		request.setFullName("Test User");
		request.setEmail("existing@example.com");
		request.setPassword("password123");

		when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> authService.register(request));

		assertEquals("Email already registered", ex.getMessage());
		verify(userRepository, never()).save(any(User.class));
		verify(jwtService, never()).generateToken(anyString());
	}

	@Test
	void login_ValidCredentials_ReturnsToken() {
		LoginRequest request = new LoginRequest();
		request.setEmail("user@example.com");
		request.setPassword("password123");

		Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", null);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(auth);
		when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");

		AuthResponse response = authService.login(request);

		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		verify(jwtService).generateToken("user@example.com");
	}
}
