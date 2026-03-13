package com.example.employeeManagement.Service;

import org.springframework.stereotype.Service;

import com.example.employeeManagement.DTO.CreateDepartmentRequest;
import com.example.employeeManagement.DTO.UpdateDepartmentRequest;
import com.example.employeeManagement.Model.Department;
import com.example.employeeManagement.Repository.DepartmentRepository;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public void createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Department already exists");
        }

        Department department = new Department();
        department.setName(request.name());
        departmentRepository.save(department);
    }

    @Transactional
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        department.setName(request.name());
        departmentRepository.save(department);
    }
}
