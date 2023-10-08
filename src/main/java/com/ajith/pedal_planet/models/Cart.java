package com.ajith.pedal_planet.models;

import com.ajith.pedal_planet.Enums.Payment;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cart_id;

    @OneToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cart")
    @ToString.Exclude
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    private String coupon_discount_amount;

    private String total_amount_AfterDiscount;



}
