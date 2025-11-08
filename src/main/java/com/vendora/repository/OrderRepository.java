package com.vendora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByDeliveryAgent(DeliveryAgent agent);
}
