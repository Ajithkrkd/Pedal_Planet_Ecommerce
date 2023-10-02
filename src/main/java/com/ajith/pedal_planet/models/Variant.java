package com.ajith.pedal_planet.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "variant_id")
    public Long id;

    private String variantName;

    private int Stock;

    private float wholesalePrice;

    private float OfferPrice;

    private float price;

    private String description;

    @ManyToOne
    @JoinColumn(name="product_id")
    private  Product product;

    private boolean  isAvailable;


    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }



}
