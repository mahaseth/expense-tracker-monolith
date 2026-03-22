package com.expense.tracker.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.expense.tracker.auth.dto.LoginRequest;
import com.expense.tracker.auth.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void register_ValidRequest_ReturnsToken() throws Exception {
		String email = "newuser" + System.currentTimeMillis() + "@example.com";
		RegisterRequest req = new RegisterRequest();
		req.setFullName("Test User");
		req.setEmail(email);
		req.setPassword("password123");
		String requestBody = objectMapper.writeValueAsString(req);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"));
	}

	@Test
	void register_InvalidEmail_ReturnsBadRequest() throws Exception {
		RegisterRequest req = new RegisterRequest();
		req.setFullName("Test User");
		req.setEmail("not-an-email");
		req.setPassword("password123");
		String requestBody = objectMapper.writeValueAsString(req);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void register_DuplicateEmail_ReturnsError() throws Exception {
		String email = "duplicate" + System.currentTimeMillis() + "@example.com";
		RegisterRequest req = new RegisterRequest();
		req.setFullName("Test User");
		req.setEmail(email);
		req.setPassword("password123");
		String requestBody = objectMapper.writeValueAsString(req);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk());

		// Duplicate registration throws IllegalArgumentException -> 400
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void login_ValidCredentials_ReturnsToken() throws Exception {
		String email = "loginuser" + System.currentTimeMillis() + "@example.com";
		String password = "password123";

		RegisterRequest regReq = new RegisterRequest();
		regReq.setFullName("Login User");
		regReq.setEmail(email);
		regReq.setPassword(password);
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regReq)))
				.andExpect(status().isOk());

		LoginRequest loginReq = new LoginRequest();
		loginReq.setEmail(email);
		loginReq.setPassword(password);
		String loginBody = objectMapper.writeValueAsString(loginReq);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.tokenType").value("Bearer"));
	}

	@Test
	void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
		LoginRequest loginReq = new LoginRequest();
		loginReq.setEmail("nonexistent@example.com");
		loginReq.setPassword("wrongpassword");
		String loginBody = objectMapper.writeValueAsString(loginReq);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginBody))
				.andExpect(status().is4xxClientError());
	}
}
