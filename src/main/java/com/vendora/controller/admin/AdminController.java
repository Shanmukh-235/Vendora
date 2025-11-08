package com.vendora.controller.admin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vendora.model.Delivery;
import com.vendora.model.Order;
import com.vendora.repository.DeliveryRepository;
import com.vendora.repository.OrderRepository;
import com.vendora.repository.ProductRepository;
import com.vendora.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    // ✅ Redirect base /admin → dashboard
    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    // ✅ Dashboard with proper total revenue (from delivered orders only)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        // 💰 Only include delivered orders
        List<Order> deliveredOrders = orderRepository.findAll()
                .stream()
                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()))
                .toList();

        BigDecimal totalRevenue = deliveredOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Add all to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        // For product overview table
        model.addAttribute("products", productRepository.findAll());

        return "admin/dashboard";
    }

    // 👥 Users list
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    // 🚚 Deliveries list
    @GetMapping("/deliveries")
    public String deliveries(Model model) {
        model.addAttribute("deliveries", deliveryRepository.findAll());
        return "admin/deliveries";
    }

    // ✅ Mark delivery as delivered (status update)
    @PostMapping("/deliveries/mark-delivered/{id}")
    public String markDelivered(@PathVariable Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow();
        delivery.setStatus("DELIVERED");
        // delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        return "redirect:/admin/deliveries";
    }
}
