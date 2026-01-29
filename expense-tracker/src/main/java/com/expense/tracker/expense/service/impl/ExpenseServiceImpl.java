package com.expense.tracker.expense.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.expense.dto.CreateExpenseRequest;
import com.expense.tracker.expense.dto.ExpenseResponse;
import com.expense.tracker.expense.dto.UpdateExpenseRequest;
import com.expense.tracker.expense.entity.Expense;
import com.expense.tracker.expense.mapper.ExpenseMapper;
import com.expense.tracker.expense.repository.ExpenseRepository;
import com.expense.tracker.expense.service.ExpenseService;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.repository.UserRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private ExpenseMapper expenseMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private User getCurrentUserEntity() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private void validateCategoryBelongsToUser(String userId, String categoryId) {
		// We validate ownership using CategoryRepository method you already have:
		// findByIdAndUserId(categoryId, userId)
		categoryRepository.findByIdAndUserId(categoryId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category (not found or not yours)"));
	}

	@Override
	public ExpenseResponse create(CreateExpenseRequest request) {
		User user = getCurrentUserEntity();

		String title = request.getTitle().trim();
		String notes = request.getNotes() == null ? null : request.getNotes().trim();
		String categoryId = request.getCategoryId().trim();

		validateCategoryBelongsToUser(user.getId(), categoryId);

		Expense expense = Expense.builder().userId(user.getId()).categoryId(categoryId).title(title)
				.amount(request.getAmount()).notes(notes).expenseDate(request.getExpenseDate()).build();

		return expenseMapper.toResponse(expenseRepository.save(expense));
	}

	@Override
	public ExpenseResponse update(String expenseId, UpdateExpenseRequest request) {
		User user = getCurrentUserEntity();

		Expense expense = expenseRepository.findByIdAndUserId(expenseId, user.getId())
				.orElseThrow(() -> new RuntimeException("Expense not found"));

		String title = request.getTitle().trim();
		String notes = request.getNotes() == null ? null : request.getNotes().trim();
		String categoryId = request.getCategoryId().trim();

		validateCategoryBelongsToUser(user.getId(), categoryId);

		expense.setTitle(title);
		expense.setAmount(request.getAmount());
		expense.setNotes(notes);
		expense.setCategoryId(categoryId);
		expense.setExpenseDate(request.getExpenseDate());

		return expenseMapper.toResponse(expenseRepository.save(expense));
	}

	@Override
	public ExpenseResponse getById(String expenseId) {
		User user = getCurrentUserEntity();

		Expense expense = expenseRepository.findByIdAndUserId(expenseId, user.getId())
				.orElseThrow(() -> new RuntimeException("Expense not found"));

		return expenseMapper.toResponse(expense);
	}

	@Override
	public List<ExpenseResponse> getAll(LocalDate from, LocalDate to, String categoryId) {
		User user = getCurrentUserEntity();

		boolean hasDateRange = (from != null && to != null);
		boolean hasCategory = (categoryId != null && !categoryId.trim().isEmpty());

		List<Expense> results;

		if (hasDateRange && hasCategory) {
			String catId = categoryId.trim();
			validateCategoryBelongsToUser(user.getId(), catId);
			results = expenseRepository.findAllByUserIdAndCategoryIdAndExpenseDateBetweenOrderByExpenseDateDesc(
					user.getId(), catId, from, to);

		} else if (hasDateRange) {
			results = expenseRepository.findAllByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(user.getId(), from,
					to);

		} else if (hasCategory) {
			String catId = categoryId.trim();
			validateCategoryBelongsToUser(user.getId(), catId);
			results = expenseRepository.findAllByUserIdAndCategoryIdOrderByExpenseDateDesc(user.getId(), catId);

		} else {
			results = expenseRepository.findAllByUserIdOrderByExpenseDateDesc(user.getId());
		}

		return results.stream().map(expenseMapper::toResponse).toList();
	}

	@Override
	public void delete(String expenseId) {
		User user = getCurrentUserEntity();

		Expense expense = expenseRepository.findByIdAndUserId(expenseId, user.getId())
				.orElseThrow(() -> new RuntimeException("Expense not found"));

		expenseRepository.delete(expense);
	}
}
