// package com.vendora.controller.admin;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.vendora.model.DeliveryAgent;
// import com.vendora.repository.DeliveryAgentRepository;
// import com.vendora.repository.DeliveryRepository;


//     @Controller
// @RequestMapping("/admin/delivery_agents")
// public class AdminDeliveryController {

//     @Autowired
//     private DeliveryAgentRepository deliveryAgentRepository;

//     @Autowired
//     private DeliveryRepository deliveryRepository;

//     @GetMapping
//     public String viewDeliveryAgents(Model model) {
//         List<DeliveryAgent> agents = deliveryAgentRepository.findAll();
//         model.addAttribute("agents", agents);
//         return "admin/delivery_agents";
//     }

//     @PostMapping("/add")
//     public String addDeliveryAgent(@ModelAttribute DeliveryAgent deliveryAgent) {
//         deliveryAgentRepository.save(deliveryAgent);
//         return "redirect:/admin/delivery_agents";
//     }

//     @GetMapping("/delete/{id}")
//     public String deleteAgent(@PathVariable Long id) {
//         deliveryAgentRepository.deleteById(id);
//         return "redirect:/admin/delivery_agents";
//     }
// }
