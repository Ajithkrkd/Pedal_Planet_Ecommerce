package com.ajith.pedal_planet.models;

import com.ajith.pedal_planet.Enums.Payment;
import com.ajith.pedal_planet.Enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity(name = "orders")
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id")
    @ToString.Exclude
    private Customer customer;

    @JsonIgnore
    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL)
    @ToString.Exclude
    List<OrderItem>orderItems = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "address_id")
    Address address;

    Payment payment;

    Status status;

    LocalDate ordered_date;

    LocalDate expecting_date;

    LocalDate shipping_date;

    private String cancellationReason;



    float total;

    @OneToOne
    @JoinColumn(name = "coupon_id")
    Coupon coupon;



}
