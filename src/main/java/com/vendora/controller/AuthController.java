package com.vendora.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vendora.model.User;
import com.vendora.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle registration submission
    @PostMapping("/register")
    public String processRegister(
        @Valid @ModelAttribute("user") User user,
        BindingResult result,
        @RequestParam(value = "profileImage", required = false) MultipartFile file,
        Model model) {

        System.out.println("Register endpoint called with email: " + user.getEmail());

        // Validation errors
        if (result.hasErrors()) {
            result.getAllErrors().forEach(System.out::println);
            return "register";
        }

        // Check duplicate email
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        try {
            // Save user via service (handles password encoding + file upload + defaults)
            userService.registerUser(user, file);

            model.addAttribute("success", "Registration successful! You can now login.");
            model.addAttribute("user", new User());

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload profile image.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Something went wrong while registering.");
        }

        return "register";
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
