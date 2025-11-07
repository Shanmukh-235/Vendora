// model/DeliveryAgent.java
package com.vendora.model;

import org.springframework.data.annotation.Transient;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String email;
    private String vehicleNumber;
    private String vehicleType;
    private boolean active = true;
    private int totalDeliveries;
    private int pendingDeliveries;
    @Transient
    private long pendingDeliveriesCount;

    @Transient
    private long completedDeliveriesCount;
}
