package com.expense.tracker.expense.mapper;

import org.springframework.stereotype.Component;

import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.entity.Expense;

@Component
public class ExpenseMapper {

	public ExpenseResponse toResponse(Expense e) {
		return new ExpenseResponse(e.getId(), e.getCategoryId(), e.getTitle(), e.getAmount(), e.getNotes(),
				e.getExpenseDate());
	}
}
