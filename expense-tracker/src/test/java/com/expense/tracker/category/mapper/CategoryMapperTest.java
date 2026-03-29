package com.expense.tracker.category.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.entity.Category;

class CategoryMapperTest {

	private final CategoryMapper mapper = new CategoryMapper();

	@Test
	void toResponse_MapsIdAndName() {
		Category c = Category.builder().id("c1").userId("u1").name("Food").build();

		CategoryResponse r = mapper.toResponse(c);

		assertEquals("c1", r.getId());
		assertEquals("Food", r.getName());
	}
}
