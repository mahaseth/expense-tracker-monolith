package com.expense.tracker.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.expense.tracker.auth.config.JwtProperties;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		JwtProperties props = new JwtProperties();
		props.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
		props.setExpirationMs(60_000L);
		jwtService = new JwtService(props);
	}

	@Test
	void generateToken_extractEmail_roundTrip() {
		String token = jwtService.generateToken("user@example.com");

		assertEquals("user@example.com", jwtService.extractEmail(token));
		assertTrue(jwtService.isTokenValid(token));
	}

	@Test
	void isTokenValid_InvalidToken_ReturnsFalse() {
		assertFalse(jwtService.isTokenValid("not-a-jwt"));
		assertFalse(jwtService.isTokenValid(""));
	}
}
