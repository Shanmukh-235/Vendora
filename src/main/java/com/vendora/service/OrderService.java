package com.vendora.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendora.model.CartItem;
import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.model.OrderItem;
import com.vendora.model.User;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.repository.OrderItemRepository;
import com.vendora.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    // 🛒 Place an order for a user from their cart items
    public Order placeOrder(User user, List<CartItem> cartItems) {
        if (user == null || cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order: user or cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus("PLACED");

        // ✅ Calculate total safely using BigDecimal
        BigDecimal total = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    // 🔍 Fetch all orders (Admin view)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔍 Fetch orders for a specific user (User dashboard)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // 🔍 Get single order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // 📊 Count total orders placed by a user
    public long countOrdersByUser(User user) {
        return orderRepository.findByUser(user).size();
    }

    // 🔍 Fetch orders assigned to a specific delivery agent
    public List<Order> getOrdersAssignedToAgent(DeliveryAgent agent) {
        if (agent == null) {
            return List.of(); // return empty list if no agent
        }
        return orderRepository.findByDeliveryAgent(agent);
    }

    // 🚚 Assign delivery agent to an order
    public void assignDeliveryAgent(Long orderId, Long agentId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElse(null);

        if (order != null && agent != null) {
            order.setDeliveryAgent(agent);
            orderRepository.save(order);
        }
    }

    // 🔄 Toggle between "PLACED" and "DELIVERED"
    public void toggleOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("PLACED");
            } else {
                order.setStatus("DELIVERED");
            }
            orderRepository.save(order);
        }
    }

    // 💰 Calculate total revenue (sum of delivered orders)
    public BigDecimal getTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
