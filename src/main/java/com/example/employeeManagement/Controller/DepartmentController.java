package com.example.employeeManagement.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.employeeManagement.DTO.CreateDepartmentRequest;
import com.example.employeeManagement.DTO.UpdateDepartmentRequest;
import com.example.employeeManagement.Service.DepartmentService;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("createDepartmentRequest", new CreateDepartmentRequest(""));
        return "departments";
    }

    @PostMapping
    public String createDepartment(@ModelAttribute("createDepartmentRequest") CreateDepartmentRequest request, RedirectAttributes redirectAttributes) {
        try {
            departmentService.createDepartment(request);
            redirectAttributes.addFlashAttribute("success", "Department created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    @PostMapping("/{id}/update")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute("updateDepartmentRequest") UpdateDepartmentRequest request, RedirectAttributes redirectAttributes) {
        try {
            departmentService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Department updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Department deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }
}
