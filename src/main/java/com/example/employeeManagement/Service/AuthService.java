package com.example.employeeManagement.Service;

import com.example.employeeManagement.DTO.CreateEmployeeRequest;
import com.example.employeeManagement.DTO.SignUpRequest;
import com.example.employeeManagement.DTO.UpdateEmployeeRequest;
import com.example.employeeManagement.Model.Department;
import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Model.Status;
import com.example.employeeManagement.Model.User;
import com.example.employeeManagement.Repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;

    public AuthService(UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
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
    public void createEmployee(@NotNull CreateEmployeeRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Department dept = null;
        if (request.departmentId() != null) {
            dept = departmentRepository.findById(request.departmentId()).orElse(null);
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);

        if (dept != null && "HR".equalsIgnoreCase(dept.getName())) {
            user.setRole(Role.HR);
        } else {
            user.setRole(Role.EMPLOYEE);
        }

        user = userRepository.save(user);

        Employee employee = new Employee();
        employee.setFullName(request.name());
        employee.setEmployeeCode(request.employeeCode());
        employee.setDepartment(dept);
        employee.setPosition(request.position());
        employee.setStatus(Status.ACTIVE);
        employee.setHireDate(LocalDate.now());
        employee.setUser(user);

        employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (request.employeeCode() != null) {
            employee.setEmployeeCode(request.employeeCode());
        }

        if (request.fullName() != null) {
            employee.setFullName(request.fullName());
            if (employee.getUser() != null) {
                employee.getUser().setName(request.fullName());
                userRepository.save(employee.getUser());
            }
        }

        if (request.departmentId() != null) {
            Department dept = departmentRepository.findById(request.departmentId()).orElse(null);
            employee.setDepartment(dept);

            if (employee.getUser() != null) {
                if (dept != null && "HR".equalsIgnoreCase(dept.getName())) {
                    employee.getUser().setRole(Role.HR);
                } else {
                    employee.getUser().setRole(Role.EMPLOYEE);
                }
            }
        }

        if (request.position() != null) {
            employee.setPosition(request.position());
        }

        if (request.hireDate() != null) {
            employee.setHireDate(request.hireDate());
        }

        if (request.status() != null) {
            employee.setStatus(request.status());
        }

        return employeeRepository.save(employee);
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
