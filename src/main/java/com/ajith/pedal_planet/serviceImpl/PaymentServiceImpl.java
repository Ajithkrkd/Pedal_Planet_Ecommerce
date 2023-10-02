package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Enums.Payment;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.Repository.PaymentRepository;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.Payment_Entity;
import com.ajith.pedal_planet.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Override
    public void savePaymentDeatils(Order order) {
        Payment_Entity payment = new Payment_Entity();
        payment.setPayment_date(LocalDate.now());
        payment.setPayment_method(String.valueOf(order.getPayment()));
        payment.setOrder_status(String.valueOf(order.getStatus()));

        boolean is_paid = false;
        if (order.getPayment() == Payment.COD) {
            payment.setPayment_method ( String.valueOf ( Payment.COD ) );
            is_paid = true;
        } else {
            payment.setPayment_method ( String.valueOf ( Payment.ONLINE ) );
            is_paid = true;
        }
        payment.setCustomer(order.getCustomer());
        payment.setTotal_amount(String.valueOf(order.getTotal()));
        payment.set_paid(is_paid);
        payment.setOrder(order);
        paymentRepository.save(payment);
    }

    @Override
    public void changePaymentStatus(Long orderId) {
      Payment_Entity existingPayment =  paymentRepository.findAllByOrder_Id(orderId);
      Optional<Order> order = orderRepository.findById(orderId);
      Order existingOrder = order.get();
      existingPayment.setOrder_status(String.valueOf(existingOrder.getStatus()));
      paymentRepository.save(existingPayment);

    }
}
