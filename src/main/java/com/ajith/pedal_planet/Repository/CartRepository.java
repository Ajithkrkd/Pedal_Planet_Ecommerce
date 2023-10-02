package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Cart;
import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {


    Optional<Cart> getCartByCustomer(Customer customer);

    Cart findAllByCustomer_Id(Long id);


}
