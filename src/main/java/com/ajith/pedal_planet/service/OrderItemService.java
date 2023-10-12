package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.OrderItem;

import java.util.List;

public interface OrderItemService  {
    void saveOrderItem(List<CartItem> cartItemList, Order order);

    void decresethQuantity(Order order);

    List< OrderItem> findAllByOrder_Id (long orderId);
}
