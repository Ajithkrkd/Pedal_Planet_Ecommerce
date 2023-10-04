package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Enums.Transaction;
import com.ajith.pedal_planet.Enums.Wallet_Method;
import com.ajith.pedal_planet.Repository.WalletHistoryRepository;
import com.ajith.pedal_planet.Repository.WalletRepository;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.models.WalletHistory;
import com.ajith.pedal_planet.service.WalletHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WalletHistoryServiceImpl implements WalletHistoryService {

    @Autowired
    private WalletHistoryRepository walletHistoryRepository;

    @Autowired
    private WalletRepository walletRepository;
    @Transactional
    public void saveWalletHistory(Wallet wallet, Customer customer, Wallet_Method method) {

        try {

            WalletHistory walletHistory = new WalletHistory();
            walletHistory.setWalletMethod(method);
            walletHistory.setWallet(wallet);
            walletHistory.setTransaction_date(LocalDate.now());
            walletHistory.setTransaction(Transaction.CREDIT);

            String amount;
            int balanceChange;

            if (method.equals(Wallet_Method.FROM_REFERRAL)) {
                amount = "200";
                balanceChange = 200;
            } else if (method.equals(Wallet_Method.JOIN_BONUS)) {
                amount = "100";
                balanceChange = 100;
            } else {
                // Handle unsupported method
                throw new IllegalArgumentException("Unsupported wallet method");
            }

            walletHistory.setAmount(amount);

            wallet.setBalance(wallet.getBalance() + balanceChange);

            walletHistoryRepository.save(walletHistory);
            walletRepository.save(wallet);
        } catch (Exception e) {
            // Handle exceptions appropriately, e.g., log the error
            throw new RuntimeException("Error saving wallet history", e);
        }
    }

    @Override
    public List<WalletHistory> getWalletHistoryByCustomerId(Long customerId) {
        return walletHistoryRepository.findByWallet_Customer_Id(customerId);
    }

    @Override
    public void saveWalletHistoryForRefund (Wallet existingWallet, Customer customer, Wallet_Method walletMethod ,float total) {
        try {
            WalletHistory walletHistory = new WalletHistory();
            walletHistory.setWalletMethod(walletMethod);
            walletHistory.setWallet(customer.getWallet ());
            walletHistory.setTransaction_date(LocalDate.now());
            walletHistory.setTransaction(Transaction.CREDIT);
            walletHistory.setAmount( String.valueOf ( total ) );
            walletHistoryRepository.save ( walletHistory );

        }
        catch (Exception e) {
            throw new RuntimeException("Error saving wallet history while refunding ", e);
        }
    }

    @Override
    public void saveWalletHistoryForPurchase (Wallet wallet, Customer existingCustomer, Wallet_Method walletMethod,float total) {
        try {
            WalletHistory walletHistory = new WalletHistory();
            walletHistory.setWalletMethod(walletMethod);
            walletHistory.setWallet(existingCustomer.getWallet ());
            walletHistory.setTransaction_date(LocalDate.now());
            walletHistory.setTransaction(Transaction.DEBIT);
            walletHistory.setAmount( String.valueOf ( total ) );
            walletHistoryRepository.save ( walletHistory );

        }
        catch (Exception e) {
            throw new RuntimeException("Error saving wallet history while refunding ", e);
        }

    }
}
