package com.expense.tracker.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.expense.tracker.category.dto.CategoryResponse;
import com.expense.tracker.category.dto.CreateCategoryRequest;
import com.expense.tracker.category.entity.Category;
import com.expense.tracker.category.mapper.CategoryMapper;
import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.category.service.impl.CategoryServiceImpl;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CategoryMapper categoryMapper;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id("user-1")
				.email("test@example.com")
				.fullName("Test User")
				.passwordHash("hash")
				.role(UserRole.USER)
				.active(true)
				.build();

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("test@example.com", null));
	}

	@Test
	void create_ValidRequest_ReturnsCategoryResponse() {
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("Food");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.existsByUserIdAndNameIgnoreCase("user-1", "Food")).thenReturn(false);

		Category savedCategory = Category.builder()
				.id("cat-1")
				.userId("user-1")
				.name("Food")
				.build();
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		CategoryResponse expectedResponse = new CategoryResponse("cat-1", "Food");
		when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

		CategoryResponse result = categoryService.create(request);

		assertEquals("cat-1", result.getId());
		assertEquals("Food", result.getName());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void create_DuplicateCategoryName_ThrowsException() {
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("Food");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.existsByUserIdAndNameIgnoreCase("user-1", "Food")).thenReturn(true);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> categoryService.create(request));

		assertEquals("Category already exists", ex.getMessage());
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void getAllForCurrentUser_ReturnsCategories() {
		Category category = Category.builder()
				.id("cat-1")
				.userId("user-1")
				.name("Food")
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findAllByUserIdOrderByNameAsc("user-1")).thenReturn(List.of(category));

		CategoryResponse response = new CategoryResponse("cat-1", "Food");
		when(categoryMapper.toResponse(category)).thenReturn(response);

		List<CategoryResponse> result = categoryService.getAllForCurrentUser();

		assertEquals(1, result.size());
		assertEquals("cat-1", result.get(0).getId());
		assertEquals("Food", result.get(0).getName());
		verify(categoryRepository).findAllByUserIdOrderByNameAsc("user-1");
	}

	@Test
	void delete_ExistingCategory_DeletesSuccessfully() {
		Category category = Category.builder()
				.id("cat-1")
				.userId("user-1")
				.name("Food")
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(category));

		categoryService.delete("cat-1");

		verify(categoryRepository).delete(category);
	}

	@Test
	void delete_NonExistentCategory_ThrowsException() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("invalid-cat", "user-1")).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class,
				() -> categoryService.delete("invalid-cat"));

		assertEquals("Category not found", ex.getMessage());
		verify(categoryRepository, never()).delete(any(Category.class));
	}
}
