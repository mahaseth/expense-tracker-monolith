package com.expense.tracker.expense.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
	private String id;
	private String categoryId;
	private String title;
	private Double amount;
	private String notes;
	private LocalDate expenseDate;
}
