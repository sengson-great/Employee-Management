package com.example.employeeManagement.DTO;

import com.example.employeeManagement.Model.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
