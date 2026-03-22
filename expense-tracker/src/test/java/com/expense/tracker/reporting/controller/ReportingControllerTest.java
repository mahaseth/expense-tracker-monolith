package com.expense.tracker.reporting.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.expense.tracker.reporting.dto.CategoryBreakdownResponse;
import com.expense.tracker.reporting.dto.MonthlyTotalResponse;
import com.expense.tracker.reporting.service.ReportingService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ReportingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReportingService reportingService;

	@Test
	void monthly_WithAuth_ReturnsMonthlyTotals() throws Exception {
		when(reportingService.getMonthlyTotals(2026))
				.thenReturn(List.of(
						new MonthlyTotalResponse(1, 500.0),
						new MonthlyTotalResponse(2, 750.0)));

		mockMvc.perform(get("/api/reports/monthly")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.param("year", "2026"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].month").value(1))
				.andExpect(jsonPath("$[0].total").value(500.0))
				.andExpect(jsonPath("$[1].month").value(2))
				.andExpect(jsonPath("$[1].total").value(750.0));

		verify(reportingService).getMonthlyTotals(2026);
	}

	@Test
	void categoryBreakdown_WithAuth_ReturnsBreakdown() throws Exception {
		LocalDate from = LocalDate.of(2026, 1, 1);
		LocalDate to = LocalDate.of(2026, 1, 31);
		when(reportingService.getCategoryBreakdown(from, to))
				.thenReturn(List.of(
						new CategoryBreakdownResponse("cat-1", "Food", 300.0),
						new CategoryBreakdownResponse("cat-2", "Transport", 150.0)));

		mockMvc.perform(get("/api/reports/category-breakdown")
				.with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
				.param("from", "2026-01-01")
				.param("to", "2026-01-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].categoryName").value("Food"))
				.andExpect(jsonPath("$[0].total").value(300.0))
				.andExpect(jsonPath("$[1].categoryName").value("Transport"))
				.andExpect(jsonPath("$[1].total").value(150.0));

		verify(reportingService).getCategoryBreakdown(from, to);
	}
}
