package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class DashBoradController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;
    @GetMapping ("")
    public String getAdminDashboard(Model model) {
        model.addAttribute ( "total_customers" ,customerService.getTotalNumberOfCustomer() );
        model.addAttribute ( "blocked_customers" ,customerService.getTotalNumberOfBlockedCustomer() );
        model.addAttribute ( "recent_customers" ,customerService.getTotalNumberOfRecentCustomer() );
            model.addAttribute ( "total_orders" ,orderService.getTotalNumberOfOrders());
            model.addAttribute ( "recent_orders" ,orderService.getTotalNumberOfRecentOrders());
            model.addAttribute ( "returned_canceled_orders" ,orderService.getTotalNumberOfReturnedAndCanceledOrders());
            model.addAttribute ( "total_sales" ,orderService.getTotalSalesAmount());
            model.addAttribute ( "total_profit" ,orderService.calculateProfitForDeliveredOrders());
            model.addAttribute ( "total_Refunds" ,orderService.getTotalRefundAmount());

        return "/admin/AdminDashboard";
    }



}
