package com.example.employeeManagement.DTO;

import com.example.employeeManagement.Model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
        @NotBlank
        String employeeCode,

        @NotBlank
        String fullName,

        @NotBlank
        String department,

        @NotBlank
        String position,

        @NotNull
        LocalDate hireDate,

        @NotNull
        Status status
) {
}
