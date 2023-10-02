package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Order;

public interface PaymentService {

    public void savePaymentDeatils(Order order);

    void changePaymentStatus(Long orderId);
}
