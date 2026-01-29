package com.expense.tracker.user.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
@Builder
public class User {
	
	@Id
    @Column(length = 36, nullable = false, updatable = false)
	private String id;
	
	@Column(nullable = false, length = 120)
	private String fullName;
	
	@Column(nullable = false, unique = true, length = 180)
	private String email;
	
	@Column(nullable = false, length = 255)
	private String passwordHash;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@Column(nullable = false)
    private Boolean active;
	
	@Column(nullable = false, updatable = false)
    private Instant createdAt;
	
	@PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = Instant.now();
        if (role == null) role = UserRole.USER;
        if (active == null) active = true;
    }
	
}
