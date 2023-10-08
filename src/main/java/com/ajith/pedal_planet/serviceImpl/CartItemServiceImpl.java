package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.CartItemRepository;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getCartItemByCart(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    @Override
    public void removeCartItem (Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCart ( cart );
        for (CartItem cartItem : cartItems) {
            cartItemRepository.delete(cartItem);
        }

        System.out.println ( cartItems);
    }


}
