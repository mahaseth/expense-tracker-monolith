package com.expense.tracker.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.dto.CreateCategoryRequest;
import com.expense.tracker.category.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	// Create a category for the logged-in user
	@PostMapping
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
		return ResponseEntity.ok(categoryService.create(request));
	}

	// Get all categories for the logged-in user
	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAll() {
		return ResponseEntity.ok(categoryService.getAllForCurrentUser());
	}

	// Delete a category (only if it belongs to logged-in user)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		categoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
