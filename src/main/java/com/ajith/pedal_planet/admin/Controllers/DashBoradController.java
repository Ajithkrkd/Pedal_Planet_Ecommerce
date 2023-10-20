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
            Optional < Float > totalsales = orderService.getTotalSalesAmount();
            if(totalsales.isPresent()){
                model.addAttribute ( "salesBool" ,true);
                model.addAttribute ( "total_sales" ,totalsales.get ());
            }else{
                model.addAttribute ( "salesBool" ,false);
            }
            model.addAttribute ( "total_profit" ,orderService.calculateProfitForDeliveredOrders());
            Optional<Float> refunds = orderService.getTotalRefundAmount ();
            if(refunds.isPresent ()){
                model.addAttribute ( "refundBool" ,true);
                model.addAttribute ( "total_Refunds" ,refunds.get ());
            }
            else {
                model.addAttribute ( "refundBool" ,true);
            }

        return "admin/AdminDashboard";
    }



}
