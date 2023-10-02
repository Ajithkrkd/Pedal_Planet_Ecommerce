package com.ajith.pedal_planet.models;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
      long cart_item_id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    private int quantity;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;


    public CartItem(Variant variant, int i, Cart cart) {
        this.variant = variant;
        this.quantity = getQuantity();
        this.setCart(cart);
    }


}
