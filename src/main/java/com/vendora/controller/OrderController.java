package com.vendora.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vendora.model.User;
import com.vendora.service.OrderService;
import com.vendora.service.UserService;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        if (user.isPresent()) {
            model.addAttribute("orders", orderService.getOrdersByUser(user.get()));
        } else {
            model.addAttribute("orders", List.of());
        }
        return "user/orders";
    }
}
