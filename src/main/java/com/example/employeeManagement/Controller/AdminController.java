package com.example.employeeManagement.Controller;

import com.example.employeeManagement.DTO.CreateEmployeeRequest;
import com.example.employeeManagement.DTO.SignUpRequest;
import com.example.employeeManagement.DTO.UpdateEmployeeRequest;
import com.example.employeeManagement.Model.Employee;
import com.example.employeeManagement.Service.AuthService;
import com.example.employeeManagement.Service.DepartmentService;
import com.example.employeeManagement.Service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public AdminController(AuthService authService, EmployeeService employeeService, DepartmentService departmentService) {
        this.authService = authService;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // Show create employee form
    @GetMapping("/create")
    public String createEmployeeForm(@NotNull Model model) {
        model.addAttribute("createEmployeeRequest", new CreateEmployeeRequest("","","","","",null,""));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "create-employee";
    }

    // Handle create employee form submission
    @PostMapping("/create")
    public String createEmployeeSubmit(@ModelAttribute("createEmployeeRequest") @Valid CreateEmployeeRequest request,
                                       @NotNull BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "create-employee";
        }
        try {
            authService.createEmployee(request);
            redirectAttributes.addFlashAttribute("success", "Employee created successfully!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "create-employee";
        }
    }

    // Show edit employee form
    @GetMapping("/{id}/edit")
    public String editEmployee(@PathVariable Long id, Model model) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return "redirect:/?error=Employee+not+found";
            }

            Long deptId = employee.getDepartment() != null ? employee.getDepartment().getId() : null;

            // Convert Employee to UpdateEmployeeRequest
            UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                    employee.getEmployeeCode(),
                    employee.getFullName(),
                    deptId,
                    employee.getPosition(),
                    employee.getHireDate(),
                    employee.getStatus()
            );

            model.addAttribute("employeeId", id);
            model.addAttribute("updateEmployeeRequest", request);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "edit-employee";
        } catch (Exception e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    // Handle edit employee form submission (use POST for forms)
    @PostMapping("/{id}/update")
    public String updateEmployee(@PathVariable Long id,
                                 @org.jetbrains.annotations.NotNull @ModelAttribute("updateEmployeeRequest") @Valid UpdateEmployeeRequest updateEmployeeRequest,
                                 @NotNull BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        System.out.println("=== UPDATE EMPLOYEE START ===");
        System.out.println("Employee ID: " + id);
        System.out.println("Form data received:");
        System.out.println("  - employeeCode: " + updateEmployeeRequest.employeeCode());
        System.out.println("  - fullName: " + updateEmployeeRequest.fullName());
        System.out.println("  - departmentId: " + updateEmployeeRequest.departmentId());
        System.out.println("  - position: " + updateEmployeeRequest.position());
        System.out.println("  - hireDate: " + updateEmployeeRequest.hireDate());
        System.out.println("  - status: " + updateEmployeeRequest.status());
        System.out.println("Validation errors: " + result.hasErrors());

        if (result.hasErrors()) {
            System.out.println("Validation errors found:");
            result.getAllErrors().forEach(error -> System.out.println("  - " + error.getDefaultMessage()));
            model.addAttribute("employeeId", id);
            return "edit-employee";
        }

        try {
            System.out.println("Calling authService.update()...");
            Employee updatedEmployee = authService.update(id, updateEmployeeRequest);
            System.out.println("Update successful! Employee ID: " + updatedEmployee.getId());

            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            System.out.println("Update failed: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employeeId", id);
            return "edit-employee";
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("employeeId", id);
            return "edit-employee";
        }
    }

    // Handle delete employee (use POST or GET for forms)
    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
}