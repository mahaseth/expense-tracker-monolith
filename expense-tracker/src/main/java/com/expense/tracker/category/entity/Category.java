package com.expense.tracker.category.entity;

import java.time.Instant;
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
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", indexes = { @Index(name = "idx_categories_user_id", columnList = "userId") })
public class Category {

	@Id
	@Column(length = 36, nullable = false, updatable = false)
	private String id;

	// IMPORTANT: microservice-friendly (store userId, not User entity)
	@Column(nullable = false, length = 36)
	private String userId;

	@Column(nullable = false, length = 80)
	private String name;

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
