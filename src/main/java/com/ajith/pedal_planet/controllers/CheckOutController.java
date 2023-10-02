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
import java.util.List;
import java.util.Optional;


@Controller
public class CheckOutController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAccountController customerAccountController;

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
    private  VariantService variantService;

    @Autowired
    private  PaymentService paymentService;


    @Autowired
    private AddressRepository addressRepository;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private CustomerCouponService customerCouponService;


    @Autowired
    private  CouponService couponService;

    @GetMapping("/placeOrder")
    public String getCheckOutPage( Model model) {



        Optional<Customer> customer = customerService.findByUsername(customerAccountController.getCurrentUsername());
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();
            Cart cart = customer.get().getCart();
            List<CartItem> cartItems = cart.getCartItems();
            String total_price_price = String.valueOf(cartservice.getTotalOfferPrice(cartItems));
            List<Address> customerAddress = addressService.getCustomerAllAddress(existingCustomer);
            model.addAttribute("cartItems" , cartItems);
            model.addAttribute("customerAddress", customerAddress);
            model.addAttribute("total_Offer_price",total_price_price );
            if(cart.getCoupon () != null){
                model.addAttribute("couponName", cart.getCoupon ().getCode ());
                model.addAttribute("total_amount_AfterDiscount", cart.getTotal_amount_AfterDiscount ());
                model.addAttribute("discount", cart.getCoupon_discount_amount ());
            }

            return "/userside/checkOutPage";
        }

        return "/userSide/checkOutPage";
    }

    @PostMapping("/showConfirmation")
    public String processOrder(@RequestParam("paymentMethod") String payment,
                               @RequestParam("ordered_address") Long address_id,
                               RedirectAttributes redirectAttributes,Model model, HttpSession session) {

        Optional<Customer> customer = customerService.findByUsername(customerAccountController.getCurrentUsername());
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
            boolean isApplied = customerCouponService.findCouponIsUsedOrNot ( Optional.ofNullable ( cart.getCoupon ( ) ),existingCustomer );
            System.out.println (isApplied + "hello world" );
            String total_price_price = String.valueOf(cartservice.getTotalOfferPrice(cart.getCartItems ()));
            model.addAttribute("total_Offer_price",total_price_price );
            model.addAttribute("total_amount_AfterDiscount", cart.getTotal_amount_AfterDiscount ());
            model.addAttribute("discount", cart.getCoupon_discount_amount ());
            model.addAttribute ( "orderItems" ,cart.getCartItems () );
                model.addAttribute ( "couponApplied" , isApplied);

            if(cart.getCoupon () != null){
                model.addAttribute ( "couponName", cart.getCoupon ().getCode () );
                model.addAttribute ( "total_amount_AfterDiscount" , cart.getTotal_amount_AfterDiscount () );
                model.addAttribute ( "discount", cart.getCoupon_discount_amount () );


            }



            Optional < Address > customerAddress = addressRepository.findById ( address_id );
            if ( customerAddress.isPresent ( ) ) {

                model.addAttribute ( "orderAddress", customerAddress.get ( ) );
                model.addAttribute ( "paymentMethod", payment );
            }
            System.out.println ("reached show confirm" );
            return "/userSide/confirmationPage";

        }else{
            return "error";
        }

    }

    @PostMapping("/saveOrder")
    @ResponseBody
    public ResponseEntity < String > saveOrder(@RequestBody OrderData orderData , HttpSession session) {
        System.out.println (orderData);
        String paymentStatus = orderData.getPaymentStatus();
        Long addressId = orderData.getAddressId();
        Optional<Customer> customer = customerService.findByUsername(customerAccountController.getCurrentUsername());
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );

            List<CartItem> cartItemList = cart.getCartItems ();

            Order order =   orderService.saveOrder(paymentStatus, addressId, cartItemList,existingCustomer,session);

            return ResponseEntity.ok("order");
        }

        return new ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
    }


        @GetMapping("/orderSuccess")

    public String getOrderSuccessPage() {
        return "/userSide/orderSuccess";
    }

    @PostMapping("/cancelOrder")
    public String cancelOrder(RedirectAttributes redirectAttributes) {
       Optional<Customer> customer = customerService.findByUsername (customerAccountController.getCurrentUsername () );
        if(customer.isPresent ()) {
            Customer existingCustomer = customer.get ( );
            Cart cart = existingCustomer.getCart ( );
            couponService.removeCouponFromCustomer (  existingCustomer ,cart.getCoupon () );
            cart.setTotal_amount_AfterDiscount ( null );
            cart.setCoupon_discount_amount ( null );
            cart.setCoupon ( null );
            cart.setTotal_amount_AfterDiscount ( null );
            cartRepository.save (cart);
            customerRepository.save (existingCustomer);
        }
        redirectAttributes.addFlashAttribute("message", "Order Cancelled");

        return "redirect:/account/orders";
    }


    @PostMapping("/process_refund")
    public String proccessRefund(@RequestParam ("paymentMethod") RefundChoice payment ){
        System.out.println(payment);
        System.out.println("hai");
        return "redirect:/account/orders";
    }

}
