package com.vendora.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.Order;
import com.vendora.model.User;
import com.vendora.service.CartService;
import com.vendora.service.OrderService;
import com.vendora.service.UserService;
import com.vendora.service.WishlistService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------- DASHBOARD ----------------
    // ---------------- DASHBOARD ----------------
    @GetMapping("/user/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // ✅ Get stats
        long orderCount = orderService.countOrdersByUser(user);
        int wishlistCount = wishlistService.getWishlistItemsCount(user);
        int cartCount = cartService.getCartItemsCount(user);

        // ✅ Fetch only the most recent order (1)
        List<Order> orders = orderService.getOrdersByUser(user).stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())) // newest first
                .limit(1)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("wishlistCount", wishlistCount);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("orders", orders);

        return "user/dashboard";
    }

    // ---------------- ORDERS ----------------
    @GetMapping("/user/orders")
    public String viewOrders(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    // ---------------- SETTINGS ----------------
    @GetMapping("/user/settings")
    public String userSettings(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user/settings";
    }

    // ---------------- PROFILE UPDATE ----------------
    @PostMapping("/user/update-profile")
    public String updateProfile(@RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);

        // Password update
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.equals(confirmPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
            } else {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/user/settings";
            }
        }

        // Profile image
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String uploadDir = "uploads/profiles/";
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }

                String fileName = user.getId() + "_" + profileImage.getOriginalFilename();
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                java.nio.file.Files.write(filePath, profileImage.getBytes());
                user.setProfileImage("/uploads/profiles/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Image upload failed. Try again!");
                return "redirect:/user/settings";
            }
        }

        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/user/settings";
    }
}
