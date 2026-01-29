package com.expense.tracker.expense.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "expenses", indexes = { @Index(name = "idx_expenses_user_id", columnList = "userId"),
		@Index(name = "idx_expenses_user_date", columnList = "userId,expenseDate"),
		@Index(name = "idx_expenses_user_category", columnList = "userId,categoryId") })
public class Expense {

	@Id
	@Column(length = 36, nullable = false, updatable = false)
	private String id;

	// Microservice-friendly: store only userId, not User entity
	@Column(nullable = false, length = 36)
	private String userId;

	// Category belongs to the same user; we validate in service
	@Column(nullable = false, length = 36)
	private String categoryId;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(length = 500)
	private String notes;

	@Column(nullable = false)
	private LocalDate expenseDate;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		if (id == null)
			id = UUID.randomUUID().toString();
		if (createdAt == null)
			createdAt = Instant.now();
	}
}
