package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Order;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    public Order saveOrder(String payment, Long address_id, List<CartItem> cartItemList, Customer existingCustomer , HttpSession session) ;


    Optional < Order > getCurrentOrderUsingOrderId (Long orderId);
}
