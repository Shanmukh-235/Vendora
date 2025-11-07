package com.vendora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vendora.model.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByEmail(String email);
}
