package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem ,Long> {
    List<OrderItem> findAllByOrder_Id(Long orderId);
}
