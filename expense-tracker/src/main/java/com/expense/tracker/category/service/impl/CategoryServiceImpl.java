package com.expense.tracker.category.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.dto.CreateCategoryRequest;
import com.expense.tracker.category.entity.Category;
import com.expense.tracker.category.mapper.CategoryMapper;
import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.category.service.CategoryService;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.repository.UserRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CategoryMapper categoryMapper;

	@Autowired
	private UserRepository userRepository;

	private User getCurrentUserEntity() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public CategoryResponse create(CreateCategoryRequest request) {
		User user = getCurrentUserEntity();
		String name = request.getName().trim();

		if (categoryRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
			throw new IllegalArgumentException("Category already exists");
		}

		Category category = Category.builder().userId(user.getId()).name(name).build();

		Category saved = categoryRepository.save(category);
		return categoryMapper.toResponse(saved);
	}

	@Override
	public List<CategoryResponse> getAllForCurrentUser() {
		User user = getCurrentUserEntity();
		return categoryRepository.findAllByUserIdOrderByNameAsc(user.getId()).stream().map(categoryMapper::toResponse)
				.toList();
	}

	@Override
	public void delete(String categoryId) {
		User user = getCurrentUserEntity();

		Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
				.orElseThrow(() -> new RuntimeException("Category not found"));

		categoryRepository.delete(category);
	}
}
