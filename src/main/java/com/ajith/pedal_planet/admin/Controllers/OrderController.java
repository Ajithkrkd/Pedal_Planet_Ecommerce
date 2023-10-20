package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.Repository.PaymentRepository;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.service.AddressService;
import com.ajith.pedal_planet.service.OrderService;
import com.ajith.pedal_planet.service.PaymentService;
import com.ajith.pedal_planet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class OrderController {


    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

/*
* List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);*/
    @GetMapping("/orders")
    public String getOrderList(Model model) {

        findPaginatedOrders ( 1,5,model );
        return "orderPages/order_management";
    }
    @GetMapping ("/orders/pages/{pageNumber}" )
    public String findPaginatedOrders (@PathVariable ( value = "pageNumber" ) int pageNumber,
                                                   @RequestParam ( value = "size", defaultValue = "5" ) int PageSize,
                                                   Model model) {

        Page < Order > page = orderService.getAllProductWithPagination ( pageNumber, PageSize );
        List < Order > orders = page.getContent ( );
        model.addAttribute ( "currentPage", pageNumber );
        model.addAttribute ( "totalPages", page.getTotalPages ( ) );
        model.addAttribute ( "totalItems", page.getTotalElements ( ) );
        model.addAttribute ( "orders", orders );
        model.addAttribute ( "size", PageSize );
        return "orderPages/order_management";
    }

    //SEARCH FOR CUSTOMER

    @GetMapping ( "/orders/pages/{pageNumber}/{size}/search-orders-result" )
    public String searchOrder (@PathVariable ( "pageNumber" ) int pageNumber,
                                   @PathVariable ( "size" ) int PageSize,
                                   @RequestParam ( "keyword" ) String keyword,
                                   Model model,
                                   Principal principal) {

        int size = PageSize;
        if ( principal == null ) {
            return "redirect:/signin";
        } else {


            Page < Order > page = orderService.searchOrder( pageNumber, size, keyword );

            List < Order > orders = page.getContent ( );
            model.addAttribute ( "totalPages", page.getTotalPages ( ) );
            model.addAttribute ( "currentPage", pageNumber );
            model.addAttribute ( "totalItems", page.getTotalElements ( ) );
            model.addAttribute ( "orders", orders );

            return "orderPages/order-management-result";
        }
    }

    @GetMapping ( "/orders/pages/{pageNumber}/{size}/filter" )
    public String searchFilteredOrder (@PathVariable ( "pageNumber" ) int pageNumber,
                                   @PathVariable ( "size" ) int PageSize,
                                   @RequestParam ( "status" ) String status,
                                   Model model,
                                   Principal principal) {

        int size = PageSize;
        if ( principal == null ) {
            return "redirect:/signin";
        } else {


            Page < Order > page = orderService.filterOrder( pageNumber, size, status );

            List < Order > orders = page.getContent ( );
            model.addAttribute ( "totalPages", page.getTotalPages ( ) );
            model.addAttribute ( "currentPage", pageNumber );
            model.addAttribute ( "totalItems", page.getTotalElements ( ) );
            model.addAttribute ( "orders", orders );

            return "orderPages/order-management-result";
        }
    }

    @GetMapping("/order_details/{order_id}")
    public String getPerticularOrderDetails(@PathVariable("order_id") Long order_id,
                                            Model model) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(order_id);

        List<Status> allStatus = Arrays.asList(Status.values());
        Optional<Order> order = orderRepository.findById(order_id);
        Status currentStatus = order.get ( ).getStatus ( );
        model.addAttribute("currentStatus", currentStatus);
        model.addAttribute("order", order);
        model.addAttribute("orderStatus", allStatus);
        model.addAttribute("orderItems", orderItems);
        if(order.get ().getCancellationReason () != null){
            model.addAttribute("cancellationReason", order.get().getCancellationReason());
        }else{
            model.addAttribute("cancellationReason", null);
        }


        return "orderPages/adminSingleOrder";

    }

    @PostMapping("/change_order_status")
    public String changeOrderStatus(@RequestParam(name = "order_id") Long orderId,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus(Status.valueOf(status));
            paymentService.changePaymentStatus(orderId);
            orderRepository.save(existingOrder);
            return "redirect:/admin/order_details/"+order.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("message", "user not fount");
            return "redirect:/admin/orders";
        }


    }

    @PostMapping("/approve_return_request/{orderId}")
    @ResponseBody
    public ResponseEntity<?> approveReturnRequest(@PathVariable("orderId") Long orderId,
                                               RedirectAttributes redirectAttributes) {
        try{
            System.out.println ("approveReturnRequest"  + orderId);
            walletService.refundTheamountToWallet(orderId);
            orderService.changeStatusToReturned(orderId);
            return ResponseEntity.ok ( "success");
        }
        catch (Exception e){
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }

    }

    @PostMapping("/approve_cancel_request/{orderId}")
    @ResponseBody
    public ResponseEntity<?> approveCancelRequest(@PathVariable("orderId") Long orderId,
                                               RedirectAttributes redirectAttributes) {

        try {
            System.out.println ("aproveCancelRequest" );
            orderService.changeStatusToCancel(orderId);
            return ResponseEntity.ok ( "success");
        }catch (Exception e) {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
