package com.expense.tracker.user.controller;

import com.expense.tracker.user.dto.UserMeResponse;
import com.expense.tracker.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserMeResponse me() {
        return userService.getCurrentUser();
    }
}
