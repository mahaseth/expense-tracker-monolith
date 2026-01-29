package com.expense.tracker.expense.service;

import java.time.LocalDate;
import java.util.List;

import com.expense.tracker.expense.dto.CreateExpenseRequest;
import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.dto.UpdateExpenseRequest;

public interface ExpenseService {

	ExpenseResponse create(CreateExpenseRequest request);

	ExpenseResponse update(String expenseId, UpdateExpenseRequest request);

	ExpenseResponse getById(String expenseId);

	List<ExpenseResponse> getAll(LocalDate from, LocalDate to, String categoryId);

	void delete(String expenseId);
}
