package com.example.employeeManagement.Controller;

import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final com.example.employeeManagement.Service.DepartmentService departmentService;

    public EmployeeController(EmployeeService employeeService, com.example.employeeManagement.Service.DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

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
        model.addAttribute("departments", departmentService.getAllDepartments());

        // Dashboard Stats
        model.addAttribute("totalCount", employeeService.getTotalEmployeesCount());
        model.addAttribute("activeCount", employeeService.getEmployeesCountByStatus(com.example.employeeManagement.Model.Status.ACTIVE));
        model.addAttribute("inactiveCount", employeeService.getEmployeesCountByStatus(com.example.employeeManagement.Model.Status.INACTIVE));

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

    @GetMapping(value = "/export", produces = "text/csv")
    @ResponseBody
    public ResponseEntity<String> exportEmployeesToCsv() {
        System.out.println("=== EXPORT EMPLOYEES TO CSV START ===");
        
        List<Employee> employees = employeeService.getAllEmployees();
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID,Employee Code,Full Name,Department,Position,Hire Date,Status\n");

        for (Employee emp : employees) {
            String hireDateStr = emp.getHireDate() != null ? emp.getHireDate().toString() : "";
            String statusStr = emp.getStatus() != null ? emp.getStatus().name() : "";
            
            // Add quotes around fields to handle any embedded commas in names/departments
            csvContent.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    emp.getId(),
                    emp.getEmployeeCode() != null ? emp.getEmployeeCode() : "",
                    emp.getFullName() != null ? emp.getFullName() : "",
                    emp.getDepartment() != null ? emp.getDepartment().getName() : "",
                    emp.getPosition() != null ? emp.getPosition() : "",
                    hireDateStr,
                    statusStr
            ));
        }

        System.out.println("Exported " + employees.size() + " employees.");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.csv\"")
                .body(csvContent.toString());
    }
}
