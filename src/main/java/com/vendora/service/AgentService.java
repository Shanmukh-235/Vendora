package com.vendora.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.repository.OrderRepository;

@Service
public class AgentService {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Fetch all orders for the agent panel
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Toggle delivery status (PLACED <-> DELIVERED)
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

    // Fetch all agents
    public List<DeliveryAgent> getAllAgents() {
        return deliveryAgentRepository.findAll();
    }
}
