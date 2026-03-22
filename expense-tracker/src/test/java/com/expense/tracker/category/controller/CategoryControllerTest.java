package com.expense.tracker.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.dto.CreateCategoryRequest;
import com.expense.tracker.category.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private CategoryService categoryService;

	@Test
	void create_WithAuth_ReturnsCreated() throws Exception {
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("Food");

		CategoryResponse response = new CategoryResponse("cat-1", "Food");
		when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/categories")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("cat-1"))
				.andExpect(jsonPath("$.name").value("Food"));

		verify(categoryService).create(any(CreateCategoryRequest.class));
	}

	@Test
	void getAll_WithAuth_ReturnsList() throws Exception {
		when(categoryService.getAllForCurrentUser()).thenReturn(
				List.of(new CategoryResponse("cat-1", "Food"), new CategoryResponse("cat-2", "Transport")));

		mockMvc.perform(get("/api/categories")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("Food"))
				.andExpect(jsonPath("$[1].name").value("Transport"));

		verify(categoryService).getAllForCurrentUser();
	}

	@Test
	void delete_WithAuth_ReturnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/categories/cat-1")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.with(csrf()))
				.andExpect(status().isNoContent());

		verify(categoryService).delete(eq("cat-1"));
	}
}
