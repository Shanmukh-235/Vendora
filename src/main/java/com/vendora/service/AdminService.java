package com.vendora.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendora.repository.OrderRepository;
import com.vendora.repository.ProductRepository;
import com.vendora.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private ProductRepository productRepository;

    @Autowired(required = false)
    private OrderRepository orderRepository;

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalProducts() {
        return productRepository != null ? productRepository.count() : 0;
    }

    public long getTotalOrders() {
        return orderRepository != null ? orderRepository.count() : 0;
    }

    public BigDecimal getTotalRevenue() {
        if (orderRepository == null) return BigDecimal.ZERO;

        return orderRepository.findAll()
            .stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
