package com.vendora.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendora.model.Agent;
import com.vendora.model.Order;
import com.vendora.repository.AgentRepository;
import com.vendora.repository.OrderRepository;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Fetch all orders for the agent panel
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Toggle order delivery status
    public void toggleDeliveryStatus(Long orderId) {
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

    // Agent management (optional)
    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }
}
