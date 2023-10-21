package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.DTO.FilterRequest;
import com.ajith.pedal_planet.Enums.Payment;
import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class userOrderController {
    @Autowired
    private OrderService orderService;


    @Autowired
    private CustomerService customerService;


    @Autowired
    private BasicServices basicServices;



    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private VariantService variantService;

    @Autowired
     private  PaymentService paymentService;

    @Autowired
    private  WalletService walletService;


    @GetMapping ("/account/orders")
    public String showUserOrders(Model model, Principal principal){
        if(principal == null ){
            return "redirect:/signin";

        }else{
            Optional < Customer > customer = customerService.findByUsername(basicServices.getCurrentUsername());
            if(customer.isPresent()){
                Customer existingCustomer = customer.get();
                List<Order> orders = existingCustomer.getOrders();

                model.addAttribute("orders" , orders);
                return "userSide/userOrders";
            }
            else{
                return "redirect:/signin";
            }
        }
    }

    //Filter orders
    @PostMapping("/account/orders")
    public String getFilteredOrders(@RequestParam ("status") String status,
                                    @RequestParam("time") String time,
                                    Model model , RedirectAttributes redirectAttributes){
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setStatus ( Status.valueOf ( status ) );
        filterRequest.setTime ( time );
        Optional < Customer > existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername () );
        if( existingCustomer.isPresent()) {
            List < Order > sortedOrders = orderService.getAllOrdersByCustomer ( existingCustomer.get ( ) );
        System.out.println (sortedOrders.size());
        List<Order> filteredOrders = orderService.filterOrders(sortedOrders, filterRequest);
        if( filteredOrders.isEmpty ( ) ){
            redirectAttributes.addFlashAttribute ( "message" ,"No Orders with this filter Result !!" );
        }
        model.addAttribute ( "orders",filteredOrders );
        return "userSide/userOrders";
        }
        return "redirect:/signin";
    }
    @GetMapping("/account/orders/sort")
    public String sortOrders(@RequestParam("sortBy") String sortBy, Model model) {
        Optional < Customer > existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername () );
        if( existingCustomer.isPresent()){


        List<Order> sortedOrders = orderService.getAllOrdersByCustomer(existingCustomer.get ());

        if ("asc".equals(sortBy)) {
            sortedOrders.sort( Comparator.comparing(Order::getOrdered_date));
        } else if ("desc".equals(sortBy)) {
            sortedOrders.sort(Comparator.comparing(Order::getOrdered_date).reversed());
        }

        model.addAttribute("orders", sortedOrders);

        return "userSide/userOrders";
        }
        return "redirect:/signin";
    }
    @GetMapping("/account/view_order/{id}")
    public String  getSingleOrderDetails(@PathVariable ("id") long order_Id,
                                         Model model,
                                         RedirectAttributes redirectAttributes){

        Optional<Order> order = orderService.findById(order_Id);
        if(order.isPresent()){
            Order existingOrder = order.get();
            List< OrderItem > orderItems = orderItemService.findAllByOrder_Id(order_Id);
            System.out.println (orderItems );
            Optional< Coupon > coupon = Optional.ofNullable ( order.get ( ).getCoupon ( ) );
            if(coupon.isPresent ()){
                Coupon coupon1 = coupon.get ();

                model.addAttribute ( "isCoupon" , true );
                model.addAttribute ( "couponName" , coupon1.getCode () );
                model.addAttribute ( "price_after_coupon" ,order.get ( ).getTotal () );
            }
            else{
                model.addAttribute ( "isCoupon" , false );
            }
            if(order.get ( ).getPayment () == Payment.WALLET){
                model.addAttribute ( "isWallet" , true);
                model.addAttribute ( "walletAmount" , order.get ( ).getTotal ());
            }else{
                model.addAttribute ( "isWallet" , false);
            }


            Address orderAddress = existingOrder.getAddress();
            model.addAttribute ( "paymentMethod" , order.get ( ).getPayment () );
            model.addAttribute("orderItems",orderItems);
            model.addAttribute("orderAddress",orderAddress);
            model.addAttribute("status",existingOrder.getStatus());
            model.addAttribute("selectedstatus",existingOrder.getStatus());
            model.addAttribute("orderId",existingOrder.getId());
            model.addAttribute ( "orderedDate", basicServices.getFormattedDate ( existingOrder.getOrdered_date () ) );
            model.addAttribute ( "shippedDate" ,basicServices.getFormattedDate ( existingOrder.getShipping_date () ) );
            model.addAttribute ( "deliveredDate",basicServices.getFormattedDate ( existingOrder.getExpecting_date () ) );
            return "userSide/userSingleOrder";

        }
        redirectAttributes.addFlashAttribute("error" ,"Order data is not fount");
        return "redirect:/account/orders";

    }

    @PostMapping("/account/update_status_to_cancel/{orderId}")
    public String ChangeOrderStatusToCancel(@PathVariable ("orderId")Long  order_id,@RequestParam("cancellationReason") String cancellationReason,
                                            RedirectAttributes redirectAttributes){
        paymentService.changePaymentStatus(order_id);
        Optional<Order> order  = orderService.findById(order_id);
        if(order.isPresent()){
            Order existingOrder = order.get();
            variantService.increaseStock(order_id);
            existingOrder.setCancellationReason (cancellationReason);
            existingOrder.setStatus ( Status.CANCEL_REQUEST_SENT );
            if(existingOrder.getPayment () == Payment.ONLINE){
                walletService.refundTheamountToWallet ( order_id );
            }
            orderService.save(existingOrder);
            redirectAttributes.addFlashAttribute("message" ,"you cancelled your order");
            return "redirect:/account/orders";
        }else{
            redirectAttributes.addFlashAttribute("error" , "order is not present");
        }

        return "redirect:/account/orders";
    }

    @PostMapping("/account/update_status_to_return/{orderId}")
    public String return_the_product(@PathVariable ("orderId")Long  order_id,
                                     @RequestParam("cancellationReason") String cancellationReason,
                                     RedirectAttributes redirectAttributes){
        Optional<Order> order  = orderService.findById(order_id);
        if(order.isPresent()){
            Order existingOrder = order.get();
            variantService.increaseStock(order_id);
            existingOrder.setStatus(Status.RETURN_REQUEST_SENT);
            existingOrder.setCancellationReason ( cancellationReason );
            orderService.save(existingOrder);
            redirectAttributes.addFlashAttribute("message" ,"you return request was sent successfully");
            return "redirect:/account/orders";
        }else{
            redirectAttributes.addFlashAttribute("error" , "order is not present");
        }

        return "redirect:/account/orders";
    }





}
