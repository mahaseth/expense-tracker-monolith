package com.expense.tracker.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.expense.tracker.expense.dto.CreateExpenseRequest;
import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.dto.UpdateExpenseRequest;
import com.expense.tracker.expense.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.findAndRegisterModules();

	@MockitoBean
	private ExpenseService expenseService;

	@Test
	void create_WithAuth_ReturnsCreated() throws Exception {
		CreateExpenseRequest request = createExpenseRequest("Groceries", 50.0, "cat-1");
		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, "notes",
				LocalDate.now());

		when(expenseService.create(any(CreateExpenseRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/expenses")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("exp-1"))
				.andExpect(jsonPath("$.title").value("Groceries"))
				.andExpect(jsonPath("$.amount").value(50.0));

		verify(expenseService).create(any(CreateExpenseRequest.class));
	}

	@Test
	void getById_WithAuth_ReturnsExpense() throws Exception {
		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null,
				LocalDate.now());
		when(expenseService.getById("exp-1")).thenReturn(response);

		mockMvc.perform(get("/api/expenses/exp-1")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("exp-1"))
				.andExpect(jsonPath("$.title").value("Groceries"));

		verify(expenseService).getById("exp-1");
	}

	@Test
	void getAll_WithAuth_ReturnsList() throws Exception {
		when(expenseService.getAll(null, null, null)).thenReturn(List.of());

		mockMvc.perform(get("/api/expenses")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER")))
				.andExpect(status().isOk());

		verify(expenseService).getAll(null, null, null);
	}

	@Test
	void update_WithAuth_ReturnsUpdated() throws Exception {
		UpdateExpenseRequest request = new UpdateExpenseRequest();
		request.setTitle("Updated");
		request.setAmount(75.0);
		request.setCategoryId("cat-1");
		request.setExpenseDate(LocalDate.now());

		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Updated", 75.0, null,
				LocalDate.now());
		when(expenseService.update(eq("exp-1"), any(UpdateExpenseRequest.class))).thenReturn(response);

		mockMvc.perform(put("/api/expenses/exp-1")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated"))
				.andExpect(jsonPath("$.amount").value(75.0));

		verify(expenseService).update(eq("exp-1"), any(UpdateExpenseRequest.class));
	}

	@Test
	void delete_WithAuth_ReturnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/expenses/exp-1")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.with(csrf()))
				.andExpect(status().isNoContent());

		verify(expenseService).delete("exp-1");
	}

	private CreateExpenseRequest createExpenseRequest(String title, double amount, String categoryId) {
		CreateExpenseRequest req = new CreateExpenseRequest();
		req.setTitle(title);
		req.setAmount(amount);
		req.setCategoryId(categoryId);
		req.setExpenseDate(LocalDate.now());
		return req;
	}
}
