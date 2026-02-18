package com.example.employeeManagement.Service;

import com.example.employeeManagement.DTO.CreateEmployeeRequest;
import com.example.employeeManagement.DTO.SignUpRequest;
import com.example.employeeManagement.DTO.UpdateEmployeeRequest;
import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Model.Status;
import com.example.employeeManagement.Model.User;
import com.example.employeeManagement.Repository.EmployeeRepository;
import com.example.employeeManagement.Repository.UserRepository;
import com.example.employeeManagement.Model.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(@NotNull SignUpRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.EMPLOYEE);
        user.setEnabled(true);

        user = userRepository.save(user);

        Employee employee = new Employee();
        employee.setFullName(request.name());
        employee.setStatus(Status.ACTIVE);
        employee.setHireDate(LocalDate.now());
        employee.setUser(user);

        employeeRepository.save(employee);
    }

    @Transactional
    public Employee createEmployee(@NotNull CreateEmployeeRequest request) {
        // Check if email exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 1. Map DTO to Entity
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);

        if ("HR".equals(request.department())) {
            user.setRole(Role.HR);
        } else {
            user.setRole(Role.EMPLOYEE);
        }

        user = userRepository.save(user);

        // Create Employee
        Employee employee = new Employee();
        employee.setFullName(request.name());
        employee.setEmployeeCode(request.employeeCode());
        employee.setDepartment(request.department());
        employee.setPosition(request.position());
        employee.setStatus(Status.ACTIVE);
        employee.setHireDate(LocalDate.now());
        employee.setUser(user);


       return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, UpdateEmployeeRequest request) {
        System.out.println("=== AUTH SERVICE UPDATE ===");
        System.out.println("Updating employee ID: " + id);
        System.out.println("Request data: " + request);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("Employee not found with ID: " + id);
                    return new IllegalArgumentException("Employee not found");
                });

        System.out.println("Found employee: " + employee.getFullName());
        System.out.println("Current values - Code: " + employee.getEmployeeCode() +
                ", Dept: " + employee.getDepartment() +
                ", Position: " + employee.getPosition());

        // Update fields
        if (request.employeeCode() != null) {
            System.out.println("Updating employeeCode from '" + employee.getEmployeeCode() +
                    "' to '" + request.employeeCode() + "'");
            employee.setEmployeeCode(request.employeeCode());
        }

        if (request.fullName() != null) {
            System.out.println("Updating fullName from '" + employee.getFullName() +
                    "' to '" + request.fullName() + "'");
            employee.setFullName(request.fullName());

            // Also update the linked user's name
            if (employee.getUser() != null) {
                System.out.println("Updating linked user name to: " + request.fullName());
                employee.getUser().setName(request.fullName());
                userRepository.save(employee.getUser());
            }
        }

        if (request.department() != null) {
            System.out.println("Updating department from '" + employee.getDepartment() +
                    "' to '" + request.department() + "'");
            employee.setDepartment(request.department());

            if (employee.getUser() != null) {
                if ("HR".equals(request.department())) {
                    employee.getUser().setRole(Role.HR);
                } else {
                    employee.getUser().setRole(Role.EMPLOYEE);
                }
            }
        }

        if (request.position() != null) {
            System.out.println("Updating position from '" + employee.getPosition() +
                    "' to '" + request.position() + "'");
            employee.setPosition(request.position());
        }

        if (request.hireDate() != null) {
            System.out.println("Updating hire date from '" + employee.getHireDate() +
                    "' to '" + request.hireDate());
            employee.setHireDate(request.hireDate());
        }

        if (request.status() != null) {
            System.out.println("Updating hire date from '" + employee.getStatus() +
                    "' to '" + request.status());
            employee.setStatus(request.status());
        }

        // Save and return
        Employee saved = employeeRepository.save(employee);
        System.out.println("Employee saved successfully! New values - Code: " + saved.getEmployeeCode() +
                ", Dept: " + saved.getDepartment() +
                ", Position: " + saved.getPosition() +
                ", Status: " + saved.getStatus());

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Delete employee and user
        if (employee.getUser() != null) {
            userRepository.delete(employee.getUser());
        }
        employeeRepository.delete(employee);
    }
}
