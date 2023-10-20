package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.DTO.Utility;
import com.ajith.pedal_planet.Enums.Wallet_Method;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.Repository.WalletHistoryRepository;
import com.ajith.pedal_planet.Repository.WalletRepository;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.models.WalletHistory;
import com.ajith.pedal_planet.service.WalletHistoryService;
import com.ajith.pedal_planet.service.WalletService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletHistoryService walletHistoryService;

    @Override
    public String generateAndReturnReferralLink() {
        String link = RandomString.make(10);
        return  link;
    }
    public  String referralLinkToshowInTheFrontEnd(HttpServletRequest request, Optional<Customer> customer){
        if(customer.isPresent()){
            Customer existingCustomer = customer.get();
            Long id = existingCustomer.getId();
           String link = existingCustomer.getReferralLink();

                return "http://pedalplanet.online/register?link=" +link;
        }
        return null;
    }

    @Override
    public void addAmountToReferredCustomer(Customer customer) {
        Wallet existingWallet = customer.getWallet();
        existingWallet.setCustomer(customer);
        walletHistoryService.saveWalletHistory(existingWallet, customer, Wallet_Method.FROM_REFERRAL);
        walletRepository.save(existingWallet);
    }

    @Override
    public void refundTheamountToWallet (Long orderId) {
        System.out.println ("hellow this is refund amount wallet method from wallet service" );
        Optional< Order > order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            Customer customer = existingOrder.getCustomer ();
            Wallet existingWallet = customer.getWallet ();
            existingWallet.setBalance (existingWallet.getBalance () + existingOrder.getTotal () );
            float total = existingOrder.getTotal();
            walletHistoryService.saveWalletHistoryForRefund(existingWallet,customer, Wallet_Method.FROM_REFUND ,total);
            walletRepository.save(existingWallet);
        }
    }

    @Override
    public void reduceAmountFromWalletAndSaveHistory (Customer existingCustomer , float total) {
        Wallet wallet = existingCustomer.getWallet ();
        wallet.setBalance ( wallet.getBalance () - total );
        walletHistoryService.saveWalletHistoryForPurchase(wallet, existingCustomer, Wallet_Method.FROM_PURCHASE, total);
        walletRepository.save ( wallet );
    }


}
