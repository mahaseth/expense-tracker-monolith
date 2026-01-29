package com.expense.tracker.category.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.tracker.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

	List<Category> findAllByUserIdOrderByNameAsc(String userId);

	Optional<Category> findByIdAndUserId(String id, String userId);

	boolean existsByUserIdAndNameIgnoreCase(String userId, String name);

	List<Category> findAllByUserIdAndIdIn(String userId, Collection<String> ids);
}
