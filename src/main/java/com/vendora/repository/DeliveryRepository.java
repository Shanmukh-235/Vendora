package com.vendora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    //List<Delivery> findByAgent(User agent);
    List<Delivery> findByDeliveryAgentId(Long deliveryAgentId);
    //List<Delivery> findByStatus(String status);
}
