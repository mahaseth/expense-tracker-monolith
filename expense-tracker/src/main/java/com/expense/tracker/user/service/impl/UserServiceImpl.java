package com.expense.tracker.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expense.tracker.user.dto.UserMeResponse;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.repository.UserRepository;
import com.expense.tracker.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public UserMeResponse getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // came from JWT subject
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserMeResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
	}

}
