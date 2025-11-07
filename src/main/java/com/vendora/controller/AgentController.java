package com.vendora.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vendora.service.AgentService;

@Controller
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    // 👀 View all orders
    @GetMapping("/orders")
    public String viewAllOrders(Model model) {
        model.addAttribute("orders", agentService.getAllOrders());
        return "agent/orders";
    }

    // 🔄 Toggle delivery status
    @GetMapping("/toggle-delivery/{id}")
    public String toggleDelivery(@PathVariable Long id) {
        agentService.toggleDeliveryStatus(id);
        return "redirect:/agent/orders";
    }
}
