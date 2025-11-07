package com.vendora.controller;

import com.vendora.model.DeliveryAgent;
import com.vendora.repository.DeliveryAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    // List all delivery agents
    @GetMapping
    public String listAgents(Model model) {
        model.addAttribute("agents", deliveryAgentRepository.findAll());
        return "admin/delivery";
    }

    // Add new delivery agent form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("agent", new DeliveryAgent());
        return "admin/add-delivery";
    }

    // Save new delivery agent
    @PostMapping("/add")
    public String addAgent(@ModelAttribute DeliveryAgent agent) {
        deliveryAgentRepository.save(agent);
        return "redirect:/admin/delivery";
    }

    // Toggle active/inactive
    @GetMapping("/toggle/{id}")
    public String toggleAgent(@PathVariable Long id) {
        deliveryAgentRepository.findById(id).ifPresent(agent -> {
            agent.setActive(!agent.isActive());
            deliveryAgentRepository.save(agent);
        });
        return "redirect:/admin/delivery";
    }

    // Delete agent
    @GetMapping("/delete/{id}")
    public String deleteAgent(@PathVariable Long id) {
        deliveryAgentRepository.deleteById(id);
        return "redirect:/admin/delivery";
    }
}
