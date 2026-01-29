package com.expense.tracker.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank
    @Size(min = 2, max = 80)
    private String name;
}
