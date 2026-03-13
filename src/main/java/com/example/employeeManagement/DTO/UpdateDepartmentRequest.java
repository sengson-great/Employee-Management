package com.example.employeeManagement.DTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateDepartmentRequest(
    @NotBlank(message = "Name is required")
    String name
) {
}
