package com.vendora.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.service.OrderService;

@Controller
@RequestMapping("/admin/delivery-agents")
public class AdminDeliveryAgentController {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private OrderService orderService;

    // 📋 Show all agents
    @GetMapping
    public String listAgents(Model model) {
        List<DeliveryAgent> agents = deliveryAgentRepository.findAll();
        model.addAttribute("agents", agents);
        model.addAttribute("newAgent", new DeliveryAgent());
        return "admin/delivery-agents";
    }

    // ➕ Add new agent
    @PostMapping("/add")
    public String addAgent(@ModelAttribute("newAgent") DeliveryAgent agent) {
        agent.setActive(true);
        if (agent.getPassword() == null || agent.getPassword().isEmpty()) {
            agent.setPassword("vendora"); // ✅ Default password
        }
        deliveryAgentRepository.save(agent);
        return "redirect:/admin/delivery-agents";
    }

    // ✏️ Update agent
    @PostMapping("/update")
    public String updateAgent(@ModelAttribute DeliveryAgent agent) {
        deliveryAgentRepository.save(agent);
        return "redirect:/admin/delivery-agents";
    }

    // 🔁 Toggle active/inactive
    @GetMapping("/toggle/{id}")
    public String toggleAgent(@PathVariable Long id) {
        deliveryAgentRepository.findById(id).ifPresent(agent -> {
            agent.setActive(!agent.isActive());
            deliveryAgentRepository.save(agent);
        });
        return "redirect:/admin/delivery-agents";
    }

    // 🗑️ Delete agent
    @GetMapping("/delete/{id}")
    public String deleteAgent(@PathVariable Long id) {
        deliveryAgentRepository.deleteById(id);
        return "redirect:/admin/delivery-agents";
    }

    // 🚚 Assign Delivery Agent to Order
    @PostMapping("/assign/{orderId}")
    public String assignAgentToOrder(@PathVariable Long orderId, @RequestParam Long agentId) {
        Order order = orderService.getOrderById(orderId);
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElse(null);

        if (order != null && agent != null) {
            order.setDeliveryAgent(agent);
            orderService.saveOrder(order);
        }

        return "redirect:/admin/orders";
    }
}
