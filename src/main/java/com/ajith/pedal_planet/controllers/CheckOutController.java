package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.DTO.OrderData;
import com.ajith.pedal_planet.Enums.RefundChoice;
import com.ajith.pedal_planet.Repository.AddressRepository;
import com.ajith.pedal_planet.Repository.CartRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class CheckOutController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BasicServices basicServices;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartService cartservice;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderService orderService;

    @Autowired
    ProductService productService;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private VariantService variantService;

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private AddressRepository addressRepository;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private CustomerCouponService customerCouponService;


    @Autowired
    private CouponService couponService;

    @Autowired
    private WalletService walletService;

    @GetMapping ( "/placeOrder" )
    public String getCheckOutPage (Model model , RedirectAttributes redirectAttributes) {
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            Cart cart = customer.get ( ).getCart ( );
            List < CartItem > cartItems = cart.getCartItems ( );
            if(cartItems.isEmpty ()){
                redirectAttributes.addFlashAttribute ( "message" , "Ooops !! you have no products in your cart");
                return "redirect:/cart";
            }
            for( CartItem item : cartItems){
                if( item.getVariant ().getStock () <= 0){
                    redirectAttributes.addFlashAttribute ( "message" , "Oops! the item ("+item.getVariant ().getProduct ().getName ()+")is not in the stock Remove it" );
                return "redirect:/cart";
                }
            }
            String total_price_price = String.valueOf ( cartservice.getTotalOfferPrice ( cartItems ) );
            List<Address> customerAddress =  customerService.getNonDeltedAddressList(existingCustomer.getId ());
            Address defualtAddress = addressService.getDefualtAddressByCustomer_Id(existingCustomer.getId ());

            model.addAttribute ( "cartItems", cartItems );
            model.addAttribute ( "customerAddress", customerAddress );
            model.addAttribute ( "customerName" , existingCustomer.getFullName ());
            model.addAttribute ( "defaultAddress", defualtAddress );
            model.addAttribute ( "total_Offer_price", total_price_price );
            if ( cart.getCoupon ( ) != null ) {
                model.addAttribute ( "couponName", cart.getCoupon ( ).getCode ( ) );
                model.addAttribute ( "total_amount_AfterDiscount", cart.getTotal_amount_AfterDiscount ( ) );
                model.addAttribute ( "discount", cart.getCoupon_discount_amount ( ) );
            }

            return "userSide/checkOutPage";
        }else{
            redirectAttributes.addFlashAttribute ( "message" ,"please login" );
            return "redirect:/signin";
        }


    }

    @PostMapping ( "/showConfirmation" )
    public String processOrder (@RequestParam ( value = "paymentMethod" ,required = false) String payment,
                                @RequestParam ( value = "ordered_address" ,required = false) Long address_id,
                                RedirectAttributes redirectAttributes, Model model, HttpSession session) {

        if(address_id == null){
            redirectAttributes.addFlashAttribute("message", "Please select a valid address. or add");
            return "redirect:/placeOrder";
        }
        if(payment == null){
            redirectAttributes.addFlashAttribute("message", "Please select Payment Method");
            return "redirect:/placeOrder";
        }
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
            boolean isApplied = customerCouponService.findCouponIsUsedOrNot ( Optional.ofNullable ( cart.getCoupon ( ) ), existingCustomer );
            System.out.println ( isApplied + "hello world" );
            String total_price_price = String.valueOf ( cartservice.getTotalOfferPrice ( cart.getCartItems ( ) ) );
            model.addAttribute ( "total_Offer_price", total_price_price );
            model.addAttribute ( "total_amount_AfterDiscount", cart.getTotal_amount_AfterDiscount ( ) );
            model.addAttribute ( "discount", cart.getCoupon_discount_amount ( ) );
            model.addAttribute ( "orderItems", cart.getCartItems ( ) );
            model.addAttribute ( "couponApplied", isApplied );

            if ( cart.getCoupon ( ) != null ) {
                model.addAttribute ( "couponName", cart.getCoupon ( ).getCode ( ) );
                model.addAttribute ( "total_amount_AfterDiscount", cart.getTotal_amount_AfterDiscount ( ) );
                model.addAttribute ( "discount", cart.getCoupon_discount_amount ( ) );


            }



            Optional < Address > customerAddress = addressRepository.findById ( address_id );
            if ( customerAddress.isPresent ( ) ) {

                model.addAttribute ( "orderAddress", customerAddress.get ( ) );
                model.addAttribute ( "paymentMethod", payment );
            }else{
                redirectAttributes.addFlashAttribute("addressNotFoundMessage", "Please select a valid address.");
                return "redirect:/placeOrder";
            }
            System.out.println ( "reached show confirm" );
            return "userSide/confirmationPage";

        } else {
            return "error";
        }

    }

    @PostMapping ( "/saveOrder" )
    @ResponseBody
    public ResponseEntity < String > saveOrder (@RequestBody OrderData orderData, HttpSession session) {
        System.out.println ( orderData );
        String paymentStatus = orderData.getPaymentStatus ( );
        Long addressId = orderData.getAddressId ( );
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
            List < CartItem > cartItemList = cart.getCartItems ( );

            Order order = orderService.saveOrder ( paymentStatus, addressId, cartItemList, existingCustomer, session );

            return ResponseEntity.ok ( "order" );
        }

        return new ResponseEntity <> ( HttpStatus.INTERNAL_SERVER_ERROR );
    }


    @GetMapping ( "/orderSuccess" )

    public String getOrderSuccessPage ( ) {
        return "userSide/orderSuccess";
    }

    @PostMapping ( "/cancelOrder" )
    public String cancelOrder (RedirectAttributes redirectAttributes) {
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
           if(existingCustomer.getCart ().getCoupon () != null){
               couponService.removeCouponFromCustomer ( existingCustomer, cart.getCoupon ( ) );
               cart.setTotal_amount_AfterDiscount ( null );
               cart.setCoupon_discount_amount ( null );
               cart.setCoupon ( null );
               cart.setTotal_amount_AfterDiscount ( null );
               cartRepository.save ( cart );
           }
            customerRepository.save ( existingCustomer );
        }
        redirectAttributes.addFlashAttribute ( "message", "Order Cancelled" );

        return "redirect:/account/orders";
    }


    @PostMapping ( "/walletPayment" )
    @ResponseBody
    public ResponseEntity < String > walletPayment (@RequestBody OrderData orderData, HttpSession session) {


        String paymentStatus = orderData.getPaymentStatus ( );
        Long addressId = orderData.getAddressId ( );
        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
            List < CartItem > cartItemList = cart.getCartItems ( );


            float total;
            if ( cart.getCoupon ( ) != null ) {
                total = Float.parseFloat ( cart.getTotal_amount_AfterDiscount ( ) );
                if ( existingCustomer.getWallet ( ).getBalance ( ) < total ) {
                    return ResponseEntity.badRequest ( ).body ( "Insufficient Balance");
                }
            } else {
                total = cartservice.getTotalOfferPrice ( cartItemList );
                if ( existingCustomer.getWallet ( ).getBalance ( ) < total ) {
                    return ResponseEntity.badRequest ( ).body ( "Insufficient Balance");
                }


                Order order = orderService.saveOrder ( paymentStatus, addressId, cartItemList, existingCustomer, session );



                Optional < Coupon > coupon = Optional.ofNullable ( cart.getCoupon ( ) );
                if ( coupon.isPresent ( ) ) {
                    total = Float.parseFloat ( cart.getTotal_amount_AfterDiscount ( ) );
                } else {
                    total = (cartservice.getTotalOfferPrice ( cartItemList ));
                }

                walletService.reduceAmountFromWalletAndSaveHistory ( existingCustomer, total );

                return ResponseEntity.ok ( "payment Successful" );
            }
        }
        return new ResponseEntity <> ( HttpStatus.INTERNAL_SERVER_ERROR );
    }

}
