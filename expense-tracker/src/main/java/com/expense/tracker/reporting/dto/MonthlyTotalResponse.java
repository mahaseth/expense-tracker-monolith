package com.expense.tracker.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyTotalResponse {
	private int month; // 1..12
	private double total; // sum of expenses in that month
}
