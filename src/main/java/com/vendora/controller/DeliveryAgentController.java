// package com.vendora.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.vendora.repository.DeliveryAgentRepository;

// @Controller
// @RequestMapping("/admin")
// public class DeliveryAgentController {

//     @Autowired
//     private DeliveryAgentRepository deliveryAgentRepository;

//     @GetMapping("/delivery_agents")
//     public String showAgents(Model model) {
//         model.addAttribute("agents", deliveryAgentRepository.findAll());
//         return "admin/delivery_agents";
//     }
// }


// // @Controller
// // @RequestMapping("/admin")
// // public class DeliveryAgentController {

// //     @Autowired
// //     private DeliveryAgentRepository deliveryAgentRepository;

// //     // Show all delivery agents + add form
// //     @GetMapping("/delivery_agents")
// //     public String viewDeliveryAgents(Model model) {
// //         List<DeliveryAgent> agents = deliveryAgentRepository.findAll();
// //         model.addAttribute("agents", agents);
// //         model.addAttribute("newAgent", new DeliveryAgent());
// //         return "admin/delivery_agents"; // Looks for templates/admin/delivery_agents.html
// //     }

// //     // Add delivery agent
// //     @PostMapping("/delivery_agents/add")
// //     public String addDeliveryAgent(@ModelAttribute("newAgent") DeliveryAgent agent) {
// //         deliveryAgentRepository.save(agent);
// //         return "redirect:/admin/delivery_agents";
// //     }

// //     // Delete delivery agent
// //     @GetMapping("/delivery_agents/delete/{id}")
// //     public String deleteAgent(@PathVariable Long id) {
// //         deliveryAgentRepository.deleteById(id);
// //         return "redirect:/admin/delivery_agents";
// //     }

// //     // Edit delivery agent (optional)
// //     @PostMapping("/delivery_agents/update")
// //     public String updateAgent(@ModelAttribute DeliveryAgent agent) {
// //         deliveryAgentRepository.save(agent);
// //         return "redirect:/admin/delivery_agents";
// //     }
// // }
