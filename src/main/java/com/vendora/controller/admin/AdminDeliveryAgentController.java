package com.vendora.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vendora.model.DeliveryAgent;
import com.vendora.repository.DeliveryAgentRepository;

@Controller
@RequestMapping("/admin/delivery-agents") // ✅ Hyphenated path for consistency
public class AdminDeliveryAgentController {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    // 📋 Show all agents + form to add new
    @GetMapping
    public String listAgents(Model model) {
        List<DeliveryAgent> agents = deliveryAgentRepository.findAll();
        model.addAttribute("agents", agents);
        model.addAttribute("newAgent", new DeliveryAgent());
        return "admin/delivery-agents"; // ✅ matches Thymeleaf file
    }

    // ➕ Add new agent
    @PostMapping("/add")
    public String addAgent(@ModelAttribute("newAgent") DeliveryAgent agent) {
        agent.setActive(true); // Default active
        deliveryAgentRepository.save(agent);
        return "redirect:/admin/delivery-agents";
    }

    // 🗑️ Delete agent
    @GetMapping("/delete/{id}")
    public String deleteAgent(@PathVariable Long id) {
        deliveryAgentRepository.deleteById(id);
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
            agent.setActive(!agent.isActive()); // flip the active flag
            deliveryAgentRepository.save(agent);
        });
        return "redirect:/admin/delivery-agents";
    }
}
