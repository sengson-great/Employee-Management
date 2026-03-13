package com.example.employeeManagement.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentRequest(
    @NotBlank(message = "Name is required")
    String name
) {
}
