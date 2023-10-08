package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.*;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Optional<Cart> getCart(Customer customer);



    void addToCartList(CartItem cartItem);

    void updateCartItem(CartItem item, int quantity);
     Cart createCart(Customer customer);

     int increaseQuantity(Long itemId);

     int decreaseQuantity(Long itemId);

    void removeFromCartList(CartItem cartItem);

    Long getTotalPrice(List<CartItem> cart);

    Long getTotalOfferPrice(List<CartItem> cart);


    void deleteCart(Cart cart);


    void removeFromTheCartAfterOrder (CartItem cartItem);
}
