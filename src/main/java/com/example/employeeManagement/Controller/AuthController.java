package com.example.employeeManagement.Controller;

import com.example.employeeManagement.DTO.SignUpRequest;
import com.example.employeeManagement.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequest("","",""));
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("signUpRequest") @Valid SignUpRequest signUpRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        try {
            authService.register(signUpRequest);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
        return "redirect:/login?signupSuccess";
    }
}
