package com.ajith.pedal_planet.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Payment_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String payment_method;

    private LocalDate payment_date;

    private String order_status;

    private String total_amount;

    private boolean is_paid;

}
