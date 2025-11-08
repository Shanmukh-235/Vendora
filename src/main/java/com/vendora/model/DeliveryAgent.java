package com.vendora.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String vehicleNumber;
    private String vehicleType;

    private boolean active = true;

    private int noOfDeliveries = 0;
    private int completedDeliveriesCount = 0;
    private int pendingDeliveriesCount = 0;
    private int pendingDeliveries = 0;
    private int totalDeliveries = 0;

    private String password = "vendora"; // ✅ default for all agents
}
