package com.expense.tracker.auth.service;

import com.expense.tracker.auth.dto.AuthResponse;
import com.expense.tracker.auth.dto.LoginRequest;
import com.expense.tracker.auth.dto.RegisterRequest;

public interface AuthService {
	
	AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
