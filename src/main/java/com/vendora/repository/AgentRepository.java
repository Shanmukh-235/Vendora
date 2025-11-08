package com.vendora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vendora.model.DeliveryAgent;

public interface AgentRepository extends JpaRepository<DeliveryAgent, Long> {
    Optional<DeliveryAgent> findByEmail(String email);
}
