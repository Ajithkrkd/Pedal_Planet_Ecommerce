package com.ajith.pedal_planet.controllers;


import com.ajith.pedal_planet.DTO.CouponResponse;
import com.ajith.pedal_planet.Repository.CartItemRepository;
import com.ajith.pedal_planet.Repository.CartRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class CouponController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;


    @Autowired
    private VariantService variantService;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired

    private CouponService couponService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerCouponService customerCouponService;

    @Autowired
    private BasicServices basicServices;

    @PostMapping ("/cart/apply-coupon")
    @ResponseBody
    public ResponseEntity <?> applyCoupon (@RequestParam (value = "couponCode",
                                            required = false) String couponCode
                                            , HttpSession session) {


        CouponResponse response = new CouponResponse();
        if (couponCode.isBlank()) {
            response.setValid(false);
            String error_message = "Enter valid coupon code";
            response.setError_message(error_message);
            return ResponseEntity.ok(response);

        } else {
            Optional < Customer > currentCustomer = customerService.findByUsername ( basicServices.getCurrentUsername ());
            Optional < Cart > customerCart = cartService.getCart ( currentCustomer.get () );
            Optional < Coupon > coupon = couponService.findByCode ( couponCode );
            boolean isApplied = customerCouponService.findCouponIsUsedOrNot ( coupon, currentCustomer.get () );

            Cart cart;
            if ( !isApplied ) {
                ResponseEntity < CouponResponse > expiredResponse = couponService.checkCouponExpired ( coupon, response );
                if ( expiredResponse != null ) return expiredResponse;

                if ( coupon.isPresent ( ) ) {
                    response.setValid ( true );
                    List < CartItem > cartItemList = customerCart.get ( ).getCartItems ( );
                    cart = customerCart.get ( );

                    couponService.addDiscountAmountToPrice ( cartItemList, coupon, response, customerCart, cart );
                    couponService.checkCouponApplicableOrNot ( customerCart, coupon, response, currentCustomer );
                } else {
                    response.setValid ( false );
                    return ResponseEntity.ok ( response );
                }

            } else {
                response.setValid ( false );
                String error_message = "Coupon is already applied";
                response.setError_message ( error_message );
                return ResponseEntity.ok ( response );
            }
            cartRepository.save ( cart );
            customerRepository.save ( currentCustomer.get () );
            return ResponseEntity.ok ( response );
        }
    }






    @PostMapping ( "/cart/delete_coupon" )
    public String deleteCoupon ( ) {
        Optional < Customer > currentCustomer = customerService.findByUsername (basicServices.getCurrentUsername ());
        Optional < Cart > customerCart = cartService.getCart ( currentCustomer.get () );
        Coupon existingCoupon = customerCart.get ( ).getCoupon ( );
        customerCart.get ( ).setCoupon ( null );
        customerCart.get ( ).setCoupon_discount_amount ( null );
        customerCart.get ( ).setTotal_amount_AfterDiscount ( null );
        couponService.removeCouponFromCustomer(currentCustomer.get (),existingCoupon);
        customerRepository.save ( currentCustomer.get () );
        cartRepository.save ( customerCart.get ( ) );
        return "redirect:/cart?delete";

    }

}
