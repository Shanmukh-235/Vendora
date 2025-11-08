// package com.vendora.controller.agent;

// import java.security.Principal;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.vendora.model.DeliveryAgent;
// import com.vendora.model.Order;
// import com.vendora.service.AgentService;
// import com.vendora.service.DeliveryAgentService; // (you’ll create this next)
// import com.vendora.service.OrderService;

// @Controller
// @RequestMapping("/agent")
// public class DeliveryAgentController {

//     @Autowired
//     private DeliveryAgentService deliveryAgentService;

//     @Autowired
//     private OrderService orderService;

//     @Autowired
//     private AgentService agentService;

//     // Dashboard for logged-in delivery agent
//     @GetMapping("/dashboard")
//     public String dashboard(Model model, Principal principal) {
//         String email = principal.getName();
//         DeliveryAgent agent = deliveryAgentService.findByEmail(email);

//         List<Order> orders = orderService.getOrdersAssignedToAgent(agent);

//         model.addAttribute("agent", agent);
//         model.addAttribute("orders", orders);

//         return "agent/dashboard"; // create templates/agent/dashboard.html
//     }

//     // Toggle order delivery status
//     @GetMapping("/order/{orderId}/toggle")
//     public String toggleOrderStatus(@PathVariable Long orderId) {
//         orderService.toggleOrderStatus(orderId);
//         return "redirect:/agent/dashboard";
//     }
// }
