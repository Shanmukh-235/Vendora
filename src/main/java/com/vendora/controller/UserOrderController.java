package com.vendora.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.User;
import com.vendora.service.OrderService;
import com.vendora.service.UserService;

@Controller
@RequestMapping("/user/orders")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // ✅ Handle "Cancel Order"
    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in first.");
            return "redirect:/login";
        }

        boolean canceled = orderService.cancelOrder(orderId, user);
        if (canceled) {
            redirectAttributes.addFlashAttribute("success", "✅ Order canceled successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to cancel order.");
        }

        return "redirect:/user/dashboard";
    }
}
