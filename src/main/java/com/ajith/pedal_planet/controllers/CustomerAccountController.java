package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.models.WalletHistory;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping ( "/account" )
public class CustomerAccountController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletHistoryService walletHistoryService;

    @Autowired
    private BasicServices basicServices;

    @Autowired
    private OtpService otpService;


    @GetMapping ( "" )
    public String listCustomerDetails (Principal principal, Model model, HttpServletRequest request) {

        if ( principal == null ) {
            return "redirect:/signin?notLogged";
        } else {

        }
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer LoggedCustomer = customer.get ( );
            List < Address > addressList = customerService.getNonDeltedAddressList ( LoggedCustomer.getId ( ) );
            Wallet wallet = (LoggedCustomer.getWallet ( ));
            if ( wallet != null ) {
                Wallet existingWallet = wallet;
                model.addAttribute ( "wallet", existingWallet );
            } else {
                model.addAttribute ( "wallet", null );
            }
            List < WalletHistory > history = walletHistoryService.getWalletHistoryByCustomerId ( customer.get ( ).getId ( ) );
            System.out.println ( history );
            model.addAttribute ( "walletHistory", history );

            LocalDate originalDate = LocalDate.now ( );
            model.addAttribute ( "link", walletService.referralLinkToshowInTheFrontEnd ( request, customer ) );
            model.addAttribute ( "currentDate", originalDate );
            model.addAttribute ( "customer", LoggedCustomer );
            model.addAttribute ( "savedAddress", addressList );

        } else {
            return "redirect:/signin?notLogged";
        }

        return "userSide/account";
    }


    @PostMapping ("/changeProfileNameAndPhoneNumber")
    public String changeProfileNameAndPhoneNumber(@RequestParam("fullName") String fullName,
                                                  @RequestParam("phoneNumber")String phoneNumber
                                                    , RedirectAttributes redirectAttributes){
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername () );
        if(customer.isPresent ()){
            Customer existingCustomer = customer.get ();
            existingCustomer.setFullName ( fullName );
            existingCustomer.setPhoneNumber ( phoneNumber );
            customerService.save(existingCustomer);
            redirectAttributes.addFlashAttribute ( "successProfile", "Profile Updated" );
            return "redirect:/account";
        }
        redirectAttributes.addFlashAttribute ( "errorProfile", "Profile Not Updated" );
        return "redirect:/account";
    }

    @PostMapping("/forgot_email")
    public String changeCustomerEmail(@RequestParam ("email") String email , RedirectAttributes redirectAttributes){
        System.out.println (email );
        return "redirect:/account";
    }
}