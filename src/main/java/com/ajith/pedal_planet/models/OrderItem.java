package com.ajith.pedal_planet.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant Variant;

    private  int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private float price;
}
