package com.ajith.pedal_planet.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Image  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="image_id")
    private Long id;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
