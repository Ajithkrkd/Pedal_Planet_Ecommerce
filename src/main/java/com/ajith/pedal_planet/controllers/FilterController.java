package com.ajith.pedal_planet.controllers;


import com.ajith.pedal_planet.DTO.FilterRequest;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FilterController {

    @Autowired
    private OrderService orderService;
    //userOrderFilter

    @PostMapping("/userOrderFilter")
    @ResponseBody
    public ResponseEntity< List < Order > > userOrderFilter(@RequestBody FilterRequest filterRequest) {

        List<Order> allOrders = orderService.getAllOrders();
        System.out.println (allOrders.size());
        List<Order> filteredOrders = orderService.filterOrders(allOrders, filterRequest);
        System.out.println ("order ajith"+filteredOrders  );
        return ResponseEntity.ok(filteredOrders);
    }

}
