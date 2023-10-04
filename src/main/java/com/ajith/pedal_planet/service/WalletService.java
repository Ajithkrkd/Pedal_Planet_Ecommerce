package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.WalletHistory;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface WalletService  {
    public String generateAndReturnReferralLink();


    public  String referralLinkToshowInTheFrontEnd(HttpServletRequest request, Optional<Customer> customer);

    void addAmountToReferredCustomer(Customer customer);


    void refundTheamountToWallet (Long orderId);

    void reduceAmountFromWalletAndSaveHistory (Customer existingCustomer,float totalAmount);
}
