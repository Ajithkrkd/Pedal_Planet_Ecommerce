package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository <Order ,Long> {
}
