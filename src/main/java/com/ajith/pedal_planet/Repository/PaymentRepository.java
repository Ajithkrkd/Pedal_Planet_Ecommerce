package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Payment_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment_Entity , Long> {
    Payment_Entity findAllByOrder_Id(Long orderId);
}
