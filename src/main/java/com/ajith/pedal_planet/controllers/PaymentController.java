package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Enums.RefundChoice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping ("/admin")
public class PaymentController {


    @GetMapping("/payment_details")
    public String getPaymentDeatils(){
        return "/orderPages/payment_management";
    }



}
