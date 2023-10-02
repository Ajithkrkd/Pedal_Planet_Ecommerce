package com.ajith.pedal_planet.models;


import com.ajith.pedal_planet.Enums.Transaction;
import com.ajith.pedal_planet.Enums.Wallet_Method;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WalletHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String amount;

    private LocalDate transaction_date;

    private Transaction transaction;

    private Wallet_Method walletMethod;

}
