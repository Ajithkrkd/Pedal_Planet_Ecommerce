package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.Enums.Wallet_Method;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.models.WalletHistory;

import java.util.List;

public interface WalletHistoryService {
    public void saveWalletHistory(Wallet wallet , Customer customer , Wallet_Method method);


    List<WalletHistory> getWalletHistoryByCustomerId(Long id);

    void saveWalletHistoryForRefund (Wallet existingWallet, Customer customer, Wallet_Method walletMethod ,float total);

    void saveWalletHistoryForPurchase (Wallet wallet, Customer existingCustomer, Wallet_Method walletMethod,float total);
}
