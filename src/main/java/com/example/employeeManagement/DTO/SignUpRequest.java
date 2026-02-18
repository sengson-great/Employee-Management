package com.example.employeeManagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Name cannot be blank") String name,
        @NotBlank(message = "Email cannot be blank") @Email String email,
        @NotBlank(message = "Password cannot be blank") @Size(min = 3, message = "Password must be at least 3 lengths") String password
) {
}
