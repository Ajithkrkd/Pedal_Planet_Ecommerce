package com.ajith.pedal_planet.models;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "customer")
    private Customer customer;

    private double balance = 0;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    @ToString.Exclude
    List<WalletHistory> history = new ArrayList<>();

}
