package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory , Long> {

    List<WalletHistory> findByWallet_Customer_Id(Long customerId);
}
