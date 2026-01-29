package com.expense.tracker.expense.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.tracker.expense.dto.CreateExpenseRequest;
import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.dto.UpdateExpenseRequest;
import com.expense.tracker.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@PostMapping
	public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
		return ResponseEntity.ok(expenseService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ExpenseResponse> update(@PathVariable String id,
			@Valid @RequestBody UpdateExpenseRequest request) {
		return ResponseEntity.ok(expenseService.update(id, request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpenseResponse> getById(@PathVariable String id) {
		return ResponseEntity.ok(expenseService.getById(id));
	}

	/**
	 * Optional filters: - from, to (both required together) - categoryId
	 *
	 * Examples: /api/expenses /api/expenses?categoryId=xxx
	 * /api/expenses?from=2026-01-01&to=2026-01-31
	 * /api/expenses?categoryId=xxx&from=2026-01-01&to=2026-01-31
	 */
	@GetMapping
	public ResponseEntity<List<ExpenseResponse>> getAll(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

			@RequestParam(required = false) String categoryId) {
		// Note: service expects from/to either both present or both null.
		return ResponseEntity.ok(expenseService.getAll(from, to, categoryId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		expenseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
