package com.expense.tracker.jpa;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.expense.tracker.category.entity.Category;
import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.expense.entity.Expense;
import com.expense.tracker.expense.repository.ExpenseRepository;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@SpringBootTest
@Transactional
class JpaEntityLifecycleTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ExpenseRepository expenseRepository;

	@Test
	void user_PrePersist_SetsIdRoleActiveAndCreatedAt() {
		User u = User.builder()
				.fullName("A")
				.email("a@b.com")
				.passwordHash("p")
				.build();

		User saved = userRepository.save(u);

		assertNotNull(saved.getId());
		assertNotNull(saved.getCreatedAt());
		assertNotNull(saved.getRole());
		assertNotNull(saved.getActive());
	}

	@Test
	void category_PrePersist_SetsIdAndCreatedAt() {
		User u = userRepository.save(User.builder()
				.fullName("A")
				.email("c@b.com")
				.passwordHash("p")
				.role(UserRole.USER)
				.active(true)
				.build());

		Category c = Category.builder().userId(u.getId()).name("Food").build();
		Category saved = categoryRepository.save(c);

		assertNotNull(saved.getId());
		assertNotNull(saved.getCreatedAt());
	}

	@Test
	void expense_PrePersist_SetsIdAndCreatedAt() {
		User u = userRepository.save(User.builder()
				.fullName("A")
				.email("e@b.com")
				.passwordHash("p")
				.role(UserRole.USER)
				.active(true)
				.build());
		Category cat = categoryRepository.save(
				Category.builder().userId(u.getId()).name("X").build());

		Expense e = Expense.builder()
				.userId(u.getId())
				.categoryId(cat.getId())
				.amount(1.0)
				.title("t")
				.expenseDate(LocalDate.now())
				.build();
		assertNull(e.getId());

		Expense saved = expenseRepository.save(e);

		assertNotNull(saved.getId());
		assertNotNull(saved.getCreatedAt());
	}
}
