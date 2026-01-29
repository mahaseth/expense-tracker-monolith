package com.expense.tracker.category.mapper;

import org.springframework.stereotype.Component;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.entity.Category;

@Component
public class CategoryMapper {

	public CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.getId(), category.getName());
	}
}
