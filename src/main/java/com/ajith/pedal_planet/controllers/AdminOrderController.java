package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.OrderRepository;
import com.ajith.pedal_planet.Repository.PaymentRepository;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.service.AddressService;
import com.ajith.pedal_planet.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {


    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    CustomerAccountController customerAccountController;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/orders")
    public String getOrderList(Model model) {
        List<Order> orders = orderRepository.findAll();

        model.addAttribute("orders", orders);
        return "orderPages/order_management";
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


        return "/orderPages/adminSingleOrder";

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
}
