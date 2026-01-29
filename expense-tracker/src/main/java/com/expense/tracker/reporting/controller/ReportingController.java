package com.expense.tracker.reporting.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.tracker.reporting.dto.CategoryBreakdownResponse;
import com.expense.tracker.reporting.dto.MonthlyTotalResponse;
import com.expense.tracker.reporting.service.ReportingService;

@RestController
@RequestMapping("/api/reports")
public class ReportingController {

	@Autowired
	private ReportingService reportingService;

	// Example: /api/reports/monthly?year=2026
	@GetMapping("/monthly")
	public ResponseEntity<List<MonthlyTotalResponse>> monthly(@RequestParam int year) {
		return ResponseEntity.ok(reportingService.getMonthlyTotals(year));
	}

	// Example: /api/reports/category-breakdown?from=2026-01-01&to=2026-01-31
	@GetMapping("/category-breakdown")
	public ResponseEntity<List<CategoryBreakdownResponse>> categoryBreakdown(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(reportingService.getCategoryBreakdown(from, to));
	}
}
