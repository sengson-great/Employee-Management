package com.example.employeeManagement.Controller;

import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Repository.EmployeeRepository;
import com.example.employeeManagement.Service.EmployeeService;
import com.example.employeeManagement.specification.EmployeeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String getEmployeeList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, // Default to first page
            Model model) {

        // Create pageable (10 items per page, sorted by ID)
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").ascending());

        Page<Employee> employeePage = employeeService.getFilteredEmployees(search, dept, status, pageable);

        model.addAttribute("employees", employeePage.getContent()); // The list of employees
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());

        // Keep your filter values for the UI
        model.addAttribute("search", search);
        model.addAttribute("selectedDept", dept);
        model.addAttribute("selectedStatus", status);

        return "employee-list";
    }

    @GetMapping("/profile/{id}")
    public String getEmployeeProfile(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);

        // Reuse your avatar color logic
        model.addAttribute("avatarColor", EmployeeController.getAvatarColor(id));

        return "employee-profile";
    }

    public static String getAvatarColor(Long id) {
        String[] colors = {
                "#3498db", "#2ecc71", "#e74c3c", "#f39c12",
                "#9b59b6", "#1abc9c", "#d35400", "#34495e",
                "#16a085", "#8e44ad", "#2c3e50", "#27ae60"
        };
        return colors[(int)(id % colors.length)];
    }
}
