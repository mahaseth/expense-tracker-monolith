package com.expense.tracker.expense.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.entity.Expense;

class ExpenseMapperTest {

	private final ExpenseMapper mapper = new ExpenseMapper();

	@Test
	void toResponse_MapsAllFields() {
		Expense e = Expense.builder()
				.id("e1")
				.userId("u1")
				.categoryId("c1")
				.title("T")
				.amount(12.5)
				.notes("n")
				.expenseDate(LocalDate.of(2026, 3, 1))
				.build();

		ExpenseResponse r = mapper.toResponse(e);

		assertEquals("e1", r.getId());
		assertEquals("c1", r.getCategoryId());
		assertEquals("T", r.getTitle());
		assertEquals(12.5, r.getAmount());
		assertEquals("n", r.getNotes());
		assertEquals(LocalDate.of(2026, 3, 1), r.getExpenseDate());
	}
}
