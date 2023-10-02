package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.models.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet , Long> {


}
