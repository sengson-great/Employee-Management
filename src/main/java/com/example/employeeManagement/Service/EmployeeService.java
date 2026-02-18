package com.example.employeeManagement.Service;

import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Repository.EmployeeRepository;
import com.example.employeeManagement.specification.EmployeeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public Page<Employee> getFilteredEmployees(String search, String dept, String status, Pageable pageable) {
        Specification<Employee> spec = EmployeeSpecifications.getEmployees(search, dept, status);
        return employeeRepository.findAll(spec, pageable);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
}
