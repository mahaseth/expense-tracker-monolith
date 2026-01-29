package com.expense.tracker.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expense.tracker.auth.dto.AuthResponse;
import com.expense.tracker.auth.dto.LoginRequest;
import com.expense.tracker.auth.dto.RegisterRequest;
import com.expense.tracker.auth.service.AuthService;
import com.expense.tracker.auth.service.JwtService;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.entity.UserRole;
import com.expense.tracker.user.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
	

	@Override
	public AuthResponse register(RegisterRequest req) {
		
		String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .fullName(req.getFullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(UserRole.USER)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
	}

	 @Override
	    public AuthResponse login(LoginRequest req) {
	        Authentication auth = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        req.getEmail().trim().toLowerCase(),
	                        req.getPassword()
	                )
	        );

	        String token = jwtService.generateToken(auth.getName());
	        return new AuthResponse(token);
	    }
	

}
