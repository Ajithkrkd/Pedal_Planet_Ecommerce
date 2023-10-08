package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Cart;
import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Order;

import java.util.List;
import java.util.Optional;

public interface CartItemService {


    List<CartItem> getCartItemByCart(Cart cart);


    void removeCartItem (Cart cart);
}
