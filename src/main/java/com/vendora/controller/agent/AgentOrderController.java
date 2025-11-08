package com.vendora.controller.agent;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.service.DeliveryAgentService;
import com.vendora.service.OrderService;

@Controller
@RequestMapping("/agent")
public class AgentOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryAgentService deliveryAgentService;

    // ✅ Unified agent dashboard — shows orders list and basic info
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // ✅ Get the currently logged-in agent’s email from session
        String email = principal.getName();

        // ✅ Fetch agent from DB
        DeliveryAgent agent = deliveryAgentService.findByEmail(email);

        if (agent == null) {
            System.out.println("⚠️ No agent found for email: " + email);
            model.addAttribute("agent", new DeliveryAgent()); // fallback to avoid nulls
        } else {
            model.addAttribute("agent", agent);
        }

        // ✅ Load orders in DESC order (newest first)
        List<Order> orders = orderService.getAllOrders()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        model.addAttribute("orders", orders);

        return "agent/dashboard";
    }

    // ✅ Toggle delivered / pending
    @GetMapping("/toggle/{id}")
    public String toggleOrderStatus(@PathVariable Long id) {
        orderService.toggleOrderStatus(id);
        return "redirect:/agent/dashboard";
    }
}
