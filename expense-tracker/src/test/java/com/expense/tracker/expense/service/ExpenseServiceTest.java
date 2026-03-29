package com.expense.tracker.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.expense.tracker.category.entity.Category;
import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.expense.dto.CreateExpenseRequest;
import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.dto.UpdateExpenseRequest;
import com.expense.tracker.expense.entity.Expense;
import com.expense.tracker.expense.mapper.ExpenseMapper;
import com.expense.tracker.expense.repository.ExpenseRepository;
import com.expense.tracker.expense.service.impl.ExpenseServiceImpl;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

	@Mock
	private ExpenseRepository expenseRepository;

	@Mock
	private ExpenseMapper expenseMapper;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private ExpenseServiceImpl expenseService;

	private User testUser;
	private Category testCategory;

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

		testCategory = Category.builder()
				.id("cat-1")
				.userId("user-1")
				.name("Food")
				.build();

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("test@example.com", null));
	}

	@Test
	void create_ValidRequest_ReturnsExpenseResponse() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setTitle("Groceries");
		request.setAmount(50.0);
		request.setCategoryId("cat-1");
		request.setExpenseDate(LocalDate.now());

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(testCategory));

		Expense savedExpense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(LocalDate.now())
				.build();
		when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

		ExpenseResponse expectedResponse = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null,
				LocalDate.now());
		when(expenseMapper.toResponse(savedExpense)).thenReturn(expectedResponse);

		ExpenseResponse result = expenseService.create(request);

		assertEquals("exp-1", result.getId());
		assertEquals("Groceries", result.getTitle());
		assertEquals(50.0, result.getAmount());
		verify(expenseRepository).save(any(Expense.class));
	}

	@Test
	void create_InvalidCategory_ThrowsException() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setTitle("Groceries");
		request.setAmount(50.0);
		request.setCategoryId("invalid-cat");
		request.setExpenseDate(LocalDate.now());

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("invalid-cat", "user-1")).thenReturn(Optional.empty());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> expenseService.create(request));

		assertEquals("Invalid category (not found or not yours)", ex.getMessage());
		verify(expenseRepository, never()).save(any(Expense.class));
	}

	@Test
	void getById_ExistingExpense_ReturnsResponse() {
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(LocalDate.now())
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findByIdAndUserId("exp-1", "user-1")).thenReturn(Optional.of(expense));

		ExpenseResponse expectedResponse = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null,
				LocalDate.now());
		when(expenseMapper.toResponse(expense)).thenReturn(expectedResponse);

		ExpenseResponse result = expenseService.getById("exp-1");

		assertEquals("exp-1", result.getId());
		verify(expenseRepository).findByIdAndUserId("exp-1", "user-1");
	}

	@Test
	void getAll_NoFilters_ReturnsAllForUser() {
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(LocalDate.now())
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findAllByUserIdOrderByExpenseDateDesc("user-1")).thenReturn(List.of(expense));

		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null,
				LocalDate.now());
		when(expenseMapper.toResponse(expense)).thenReturn(response);

		List<ExpenseResponse> result = expenseService.getAll(null, null, null);

		assertEquals(1, result.size());
		assertEquals("exp-1", result.get(0).getId());
		verify(expenseRepository).findAllByUserIdOrderByExpenseDateDesc("user-1");
	}

	@Test
	void delete_ExistingExpense_DeletesSuccessfully() {
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(LocalDate.now())
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findByIdAndUserId("exp-1", "user-1")).thenReturn(Optional.of(expense));

		expenseService.delete("exp-1");

		verify(expenseRepository).delete(expense);
	}

	@Test
	void update_ValidRequest_PersistsChanges() {
		LocalDate day = LocalDate.of(2026, 3, 1);
		UpdateExpenseRequest request = new UpdateExpenseRequest();
		request.setTitle(" Updated ");
		request.setAmount(99.0);
		request.setCategoryId(" cat-1 ");
		request.setNotes(" note ");
		request.setExpenseDate(day);

		Expense existing = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Old")
				.amount(1.0)
				.expenseDate(day)
				.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findByIdAndUserId("exp-1", "user-1")).thenReturn(Optional.of(existing));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(testCategory));
		when(expenseRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0));

		ExpenseResponse mapped = new ExpenseResponse("exp-1", "cat-1", "Updated", 99.0, "note", day);
		when(expenseMapper.toResponse(existing)).thenReturn(mapped);

		ExpenseResponse result = expenseService.update("exp-1", request);

		assertEquals("Updated", result.getTitle());
		assertEquals(99.0, result.getAmount());
		verify(expenseRepository).save(existing);
	}

	@Test
	void update_InvalidCategory_Throws() {
		UpdateExpenseRequest request = new UpdateExpenseRequest();
		request.setTitle("T");
		request.setAmount(1.0);
		request.setCategoryId("bad");
		request.setExpenseDate(LocalDate.now());

		Expense existing = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Old")
				.amount(1.0)
				.expenseDate(LocalDate.now())
				.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findByIdAndUserId("exp-1", "user-1")).thenReturn(Optional.of(existing));
		when(categoryRepository.findByIdAndUserId("bad", "user-1")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> expenseService.update("exp-1", request));
	}

	@Test
	void getById_NotFound_Throws() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findByIdAndUserId("missing", "user-1")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> expenseService.getById("missing"));
	}

	@Test
	void getAll_DateRangeOnly_UsesDateQuery() {
		LocalDate from = LocalDate.of(2026, 1, 1);
		LocalDate to = LocalDate.of(2026, 1, 31);
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(from)
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(expenseRepository.findAllByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc("user-1", from, to))
				.thenReturn(List.of(expense));

		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null, from);
		when(expenseMapper.toResponse(expense)).thenReturn(response);

		List<ExpenseResponse> result = expenseService.getAll(from, to, null);

		assertEquals(1, result.size());
		verify(expenseRepository).findAllByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc("user-1", from, to);
	}

	@Test
	void getAll_CategoryOnly_UsesCategoryQuery() {
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(LocalDate.now())
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(testCategory));
		when(expenseRepository.findAllByUserIdAndCategoryIdOrderByExpenseDateDesc("user-1", "cat-1"))
				.thenReturn(List.of(expense));

		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null,
				LocalDate.now());
		when(expenseMapper.toResponse(expense)).thenReturn(response);

		List<ExpenseResponse> result = expenseService.getAll(null, null, " cat-1 ");

		assertEquals(1, result.size());
		verify(expenseRepository).findAllByUserIdAndCategoryIdOrderByExpenseDateDesc("user-1", "cat-1");
	}

	@Test
	void getAll_DateRangeAndCategory_UsesCombinedQuery() {
		LocalDate from = LocalDate.of(2026, 1, 1);
		LocalDate to = LocalDate.of(2026, 1, 31);
		Expense expense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.expenseDate(from)
				.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(testCategory));
		when(expenseRepository.findAllByUserIdAndCategoryIdAndExpenseDateBetweenOrderByExpenseDateDesc(
				"user-1", "cat-1", from, to)).thenReturn(List.of(expense));

		ExpenseResponse response = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, null, from);
		when(expenseMapper.toResponse(expense)).thenReturn(response);

		List<ExpenseResponse> result = expenseService.getAll(from, to, "cat-1");

		assertEquals(1, result.size());
		verify(expenseRepository).findAllByUserIdAndCategoryIdAndExpenseDateBetweenOrderByExpenseDateDesc(
				"user-1", "cat-1", from, to);
	}

	@Test
	void create_WithNotes_TrimsNotes() {
		CreateExpenseRequest request = new CreateExpenseRequest();
		request.setTitle(" Groceries ");
		request.setAmount(50.0);
		request.setCategoryId("cat-1");
		request.setNotes("  note  ");
		request.setExpenseDate(LocalDate.now());

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(categoryRepository.findByIdAndUserId("cat-1", "user-1")).thenReturn(Optional.of(testCategory));

		Expense savedExpense = Expense.builder()
				.id("exp-1")
				.userId("user-1")
				.categoryId("cat-1")
				.title("Groceries")
				.amount(50.0)
				.notes("note")
				.expenseDate(LocalDate.now())
				.build();
		when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

		ExpenseResponse expectedResponse = new ExpenseResponse("exp-1", "cat-1", "Groceries", 50.0, "note",
				LocalDate.now());
		when(expenseMapper.toResponse(savedExpense)).thenReturn(expectedResponse);

		ExpenseResponse result = expenseService.create(request);

		assertEquals("note", result.getNotes());
	}
}
