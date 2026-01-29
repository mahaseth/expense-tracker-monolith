package com.expense.tracker.expense.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateExpenseRequest {

	@NotBlank
	@Size(min = 2, max = 120)
	private String title;

	@NotNull
	@Positive
	private Double amount;

	@Size(max = 500)
	private String notes;

	@NotBlank
	private String categoryId;

	@NotNull
	private LocalDate expenseDate;
}
