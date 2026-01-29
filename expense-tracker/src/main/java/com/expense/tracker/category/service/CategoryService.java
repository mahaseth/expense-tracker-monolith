package com.expense.tracker.category.service;

import java.util.List;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.dto.CreateCategoryRequest;

public interface CategoryService {

	CategoryResponse create(CreateCategoryRequest request);

	List<CategoryResponse> getAllForCurrentUser();

	void delete(String categoryId);
}
