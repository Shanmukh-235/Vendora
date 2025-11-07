package com.vendora.controller.admin;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vendora.model.Delivery;
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

    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Dashboard stats
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        // Assuming Order has a field "totalPrice" of type BigDecimal or double
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(order -> {
                    try {
                        // Try to call getTotalPrice() reflectively if it exists
                        var method = order.getClass().getMethod("getTotalPrice");
                        Object value = method.invoke(order);
                        if (value instanceof BigDecimal)
                            return (BigDecimal) value;
                        if (value instanceof Number)
                            return BigDecimal.valueOf(((Number) value).doubleValue());
                    } catch (Exception ignored) {
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        // Optionally include product list for dashboard display
        model.addAttribute("products", productRepository.findAll());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/deliveries")
    public String deliveries(Model model) {
        model.addAttribute("deliveries", deliveryRepository.findAll());
        return "admin/deliveries";
    }

    @PostMapping("/deliveries/mark-delivered/{id}")
    public String markDelivered(@PathVariable Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow();
        delivery.setStatus("Delivered");
        //delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        return "redirect:/admin/deliveries";
    }
}
