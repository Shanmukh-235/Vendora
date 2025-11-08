package com.vendora.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vendora.model.DeliveryAgent;
import com.vendora.repository.AgentRepository;

@Controller
@RequestMapping("/admin/agents")
public class AdminAgentController {

    @Autowired
    private AgentRepository agentRepository;

    // 👀 List all agents
    @GetMapping
    public String viewAgents(Model model) {
        model.addAttribute("agents", agentRepository.findAll());
        return "admin/agents";
    }

    // ➕ Add new agent
    @PostMapping("/add")
    public String addAgent(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam(required = false) String phone) {

        DeliveryAgent agent = new DeliveryAgent();
        agent.setName(name);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setActive(true);
        agent.setPassword("vendora"); // default password for all

        agentRepository.save(agent);
        return "redirect:/admin/agents";
    }

    // 🔄 Toggle active/inactive status
    @GetMapping("/toggle/{id}")
    public String toggleActive(@org.springframework.web.bind.annotation.PathVariable Long id) {
        agentRepository.findById(id).ifPresent(agent -> {
            agent.setActive(!agent.isActive());
            agentRepository.save(agent);
        });
        return "redirect:/admin/agents";
    }
}
