package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.Repository.AddressRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/account")
public class CustomerAccountController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;


    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private VariantService variantService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletHistoryService walletHistoryService;

    @Autowired
    private BasicServices basicServices;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        return authentication.getName();
    }

    @GetMapping("")
    public String listCustomerDetails(Principal principal, Model model , HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/signin?notLogged";
        } else {

        }
        Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
        if (customer.isPresent()) {
            Customer LoggedCustomer = customer.get();
            List<Address> addressList =  customerService.getNonDeltedAddressList(LoggedCustomer.getId ());
            Wallet wallet = (LoggedCustomer.getWallet());
            if (wallet != null) {
                Wallet existingWallet = wallet;
                model.addAttribute("wallet" ,existingWallet);
            }else {
                model.addAttribute("wallet",null);
            }
                List<WalletHistory> history = walletHistoryService .getWalletHistoryByCustomerId(customer.get().getId());
                System.out.println(history);
                model.addAttribute("walletHistory",history);
            LocalDate originalDate = LocalDate.now();
            model.addAttribute("link",walletService.referralLinkToshowInTheFrontEnd(request,customer));
            model.addAttribute("currentDate" ,originalDate);
            model.addAttribute("customer", LoggedCustomer);
            model.addAttribute("savedAddress" ,addressList);

        } else {
            return "redirect:/signin?notLogged";
        }

        return "/userSide/account";
    }


    /**
     * @param currentPassword
     * @param newPassword
     * @param confirmPassword
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/forgotten_password")
    public String forgottenPassword(@RequestParam("currentPassword") String currentPassword,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    RedirectAttributes redirectAttributes) {

        Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
        if (customer.isPresent()) {
            Customer loggedInCustomer = customer.get();

            if (passwordEncoder.matches(currentPassword, loggedInCustomer.getPassword())) {
                if (newPassword.equals(confirmPassword)) {
                    String hashedPassword = passwordEncoder.encode(newPassword);
                    loggedInCustomer.setPassword(hashedPassword);
                    customerRepository.save(loggedInCustomer);
                    redirectAttributes.addFlashAttribute("message", "Password changed successfully.");
                    return "redirect:/account";
                } else {
                    redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match.");
                    return "redirect:/account";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
                return "redirect:/account";
            }


        } else {
            return "redirect:/signin?notLogged";
        }
    }

    @GetMapping("/orders")
    public String showUserOrders(Model model, Principal principal){
        if(principal == null ){
            return "redirect:/signin";

        }else{
            Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
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


    @GetMapping("/view_order/{id}")
    public String  getSingleOrderDetails(@PathVariable("id") long order_Id,
                                        Model model,
                                        RedirectAttributes redirectAttributes){

       Optional<Order> order = orderRepository.findById(order_Id);
       if(order.isPresent()){
           Order existingOrder = order.get();
           List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(order_Id);
           System.out.println (orderItems );
           Address orderAddress = existingOrder.getAddress();
           model.addAttribute("orderItems",orderItems);
           model.addAttribute("orderAddress",orderAddress);
           model.addAttribute("status",existingOrder.getStatus());
           model.addAttribute("selectedstatus",existingOrder.getStatus());
           model.addAttribute("orderId",existingOrder.getId());
           model.addAttribute ( "orderedDate", basicServices.getFormattedDate ( existingOrder.getOrdered_date () ) );
           model.addAttribute ( "shippedDate" ,basicServices.getFormattedDate ( existingOrder.getShipping_date () ) );
           model.addAttribute ( "deliveredDate",basicServices.getFormattedDate ( existingOrder.getExpecting_date () ) );
           return "/userSide/userSingleOrder";

       }
       redirectAttributes.addFlashAttribute("error" ,"Order data is not fount");
        return "redirect:/orders";

    }

    @PostMapping("/update_status_to_cancel/{orderId}")
    public String ChangeOrderStatusToCancel(@PathVariable ("orderId")Long  order_id,@RequestParam("cancellationReason") String cancellationReason,
                                            RedirectAttributes redirectAttributes){
        paymentService.changePaymentStatus(order_id);
        Optional<Order> order  = orderRepository.findById(order_id);
        if(order.isPresent()){
            Order existingOrder = order.get();
            existingOrder.setStatus(Status.CANCELLED);
            variantService.increaseStock(order_id);
            existingOrder.setCancellationReason (cancellationReason);
            existingOrder.setStatus ( Status.CANCEL_REQUEST_SENT );
            orderRepository.save(existingOrder);
            redirectAttributes.addFlashAttribute("message" ,"you cancelled your order");
            return "redirect:/account/orders";
        }else{
            redirectAttributes.addFlashAttribute("error" , "order is not present");
        }

        return "redirect:/account/orders";
    }

    @PostMapping("/update_status_to_return/{orderId}")
    public String return_the_product(@PathVariable ("orderId")Long  order_id,
                                     @RequestParam("cancellationReason") String cancellationReason,
                                     RedirectAttributes redirectAttributes){
        Optional<Order> order  = orderRepository.findById(order_id);
        if(order.isPresent()){
            Order existingOrder = order.get();
            existingOrder.setStatus(Status.RETURN_REQUEST_SENT);
            existingOrder.setCancellationReason ( cancellationReason );

            orderRepository.save(existingOrder);
            redirectAttributes.addFlashAttribute("message" ,"you return request was sent successfully");
            return "redirect:/account/orders";
        }else{
            redirectAttributes.addFlashAttribute("error" , "order is not present");
        }

        return "redirect:/account/orders";
    }


    @PostMapping("/make_default/{addressId}")
    @ResponseBody
    public ResponseEntity<String> updateDefaultAddress(@PathVariable Long addressId) {

        System.out.println(addressId);
        return null;
    }
}



