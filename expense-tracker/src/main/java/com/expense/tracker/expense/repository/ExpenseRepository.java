package com.expense.tracker.expense.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.tracker.expense.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, String> {

	Optional<Expense> findByIdAndUserId(String id, String userId);

	List<Expense> findAllByUserIdOrderByExpenseDateDesc(String userId);

	List<Expense> findAllByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(String userId, LocalDate from,
			LocalDate to);

	List<Expense> findAllByUserIdAndCategoryIdOrderByExpenseDateDesc(String userId, String categoryId);

	List<Expense> findAllByUserIdAndCategoryIdAndExpenseDateBetweenOrderByExpenseDateDesc(String userId,
			String categoryId, LocalDate from, LocalDate to);
}
