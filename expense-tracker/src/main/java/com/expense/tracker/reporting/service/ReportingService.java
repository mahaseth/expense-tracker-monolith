package com.expense.tracker.reporting.service;

import java.time.LocalDate;
import java.util.List;

import com.expense.tracker.reporting.dto.CategoryBreakdownResponse;
import com.expense.tracker.reporting.dto.MonthlyTotalResponse;

public interface ReportingService {

	List<MonthlyTotalResponse> getMonthlyTotals(int year);

	List<CategoryBreakdownResponse> getCategoryBreakdown(LocalDate from, LocalDate to);
}
