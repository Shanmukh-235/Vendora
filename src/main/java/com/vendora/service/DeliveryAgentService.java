package com.vendora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vendora.model.DeliveryAgent;
import com.vendora.repository.DeliveryAgentRepository;

@Service
public class DeliveryAgentService {

    private final DeliveryAgentRepository deliveryAgentRepository;

    public DeliveryAgentService(DeliveryAgentRepository deliveryAgentRepository) {
        this.deliveryAgentRepository = deliveryAgentRepository;
    }

    public List<DeliveryAgent> getAllAgents() {
        return deliveryAgentRepository.findAll();
    }

    public DeliveryAgent saveAgent(DeliveryAgent agent) {
        return deliveryAgentRepository.save(agent);
    }

    public DeliveryAgent getAgentById(Long id) {
        return deliveryAgentRepository.findById(id).orElse(null);
    }

    public void deleteAgent(Long id) {
        deliveryAgentRepository.deleteById(id);
    }
}
