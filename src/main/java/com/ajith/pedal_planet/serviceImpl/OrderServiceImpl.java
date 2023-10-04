package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.DTO.FilterRequest;
import com.ajith.pedal_planet.Enums.Payment;
import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.Repository.CartRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentService paymentService;

    public Order saveOrder(String payment, Long address_id, List<CartItem> cartItemList, Customer existingCustomer,
                           HttpSession session) {
        Optional<Address> address = addressService.findById(address_id);
        Long total_OfferPrice = cartService.getTotalOfferPrice(cartItemList);

        Cart cart = existingCustomer.getCart ();

        // Create and save the Order entity
        Order order = new Order();
        if( !(cart.getCoupon () == null) ) {
            order.setCoupon(cart.getCoupon ());
            order.setTotal ( Float.parseFloat ( cart.getTotal_amount_AfterDiscount () ) );
            session.setAttribute ( "total_Price" , Float.parseFloat ( cart.getTotal_amount_AfterDiscount () )  );
            couponService.decreaseCouponStock(cart.getCoupon());
        }else{
            order.setTotal(total_OfferPrice);
            session.setAttribute ( "total_Price" ,total_OfferPrice );
        }
        order.setOrdered_date(LocalDate.now());
        LocalDate modifiedDate = order.getOrdered_date().plus(7, ChronoUnit.DAYS);
        LocalDate shippingDate = order.getOrdered_date().plus(3, ChronoUnit.DAYS);


        order.setShipping_date ( shippingDate );
        order.setExpecting_date(modifiedDate);
        order.setStatus(Status.PENDING);
        order.setCustomer(existingCustomer);
        order.setAddress(address.get());
        order.setPayment(Payment.valueOf(payment));

        List<OrderItem> orderItems = new ArrayList <> ();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setVariant (cartItem.getVariant ());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getVariant ().getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

        }
        order.setOrderItems ( orderItems );
        orderRepository.save(order);
        cart.setCoupon_discount_amount ( null );
        cart.setTotal_amount_AfterDiscount ( null );
        cart.setCoupon ( null );
        cartRepository.save(cart);
        orderRepository.save(order);
        paymentService.savePaymentDeatils ( order );
        customerRepository.save(existingCustomer);


        return order;
    }

    @Override
    public Optional < Order > getCurrentOrderUsingOrderId (Long orderId) {
        return orderRepository.findById (orderId);
    }

    @Override
    public void changeStatusToReturned (Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.get().setStatus(Status.RETURNED);
        orderRepository.save(order.get());
    }

    @Override
    public List < Order > getAllOrders ( ) {
        return orderRepository.findAll ();
    }

    @Override
    public List<Order> filterOrders(List<Order> orders, FilterRequest filterRequest) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            System.out.println ("Filtering order in loop" );
            if (filterRequest.getStatusFilters().contains(order.getStatus())) {

                    filteredOrders.add(order);
                System.out.println ("if " );
            }
        }

        return filteredOrders;
    }


}
