package com.expense.tracker.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryBreakdownResponse {
	private String categoryId;
	private String categoryName;
	private double total;
}
