package com.expense.tracker.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMeResponse {

    private String id;
    private String fullName;
    private String email;
    private String role;
}
