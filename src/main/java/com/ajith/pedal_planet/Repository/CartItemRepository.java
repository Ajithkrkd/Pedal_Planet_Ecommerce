package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Cart;
import com.ajith.pedal_planet.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository < CartItem, Long > {


    List < CartItem > findByCart (Cart cartId);


}
