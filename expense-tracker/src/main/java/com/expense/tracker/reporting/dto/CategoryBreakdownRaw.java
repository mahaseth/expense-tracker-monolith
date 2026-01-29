package com.expense.tracker.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryBreakdownRaw {
	private String categoryId;
	private double total;
}
