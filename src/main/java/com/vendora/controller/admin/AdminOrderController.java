package com.vendora.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vendora.model.Order;
import com.vendora.model.DeliveryAgent;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.service.OrderService;

@Controller
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    /**
     * ✅ Show all orders with user + delivery agent info.
     */
    @GetMapping("/admin/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<DeliveryAgent> agents = deliveryAgentRepository.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("agents", agents);
        return "admin/orders";
    }

    /**
     * ✅ Assign or change the delivery agent for a specific order.
     */
    @PostMapping("/admin/orders/assign")
    public String assignDeliveryAgent(@RequestParam Long orderId,
                                      @RequestParam Long agentId) {
        orderService.assignDeliveryAgent(orderId, agentId);
        return "redirect:/admin/orders";
    }

    /**
     * ✅ Toggle order delivery status between PENDING and DELIVERED.
     */
    @GetMapping("/admin/orders/toggle-status/{id}")
    public String toggleOrderStatus(@PathVariable Long id) {
        orderService.toggleOrderStatus(id);
        return "redirect:/admin/orders";
    }
}
