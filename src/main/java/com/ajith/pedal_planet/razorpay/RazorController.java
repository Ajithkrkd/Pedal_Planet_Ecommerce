package com.ajith.pedal_planet.razorpay;



import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.OrderService;
import com.razorpay.RazorpayException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class RazorController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    CustomerService customerService;

    @Autowired
    OrderService orderService;

    @Value("${razorpay.keyId}")
    private String keyId;


    @Value ( "${razorpay.keySecret}" )
    private String keySecret;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody String total, Model model, HttpServletRequest request) {
        Customer user = customerService.findByUsername(getCurrentUsername()).orElse(null);

        float theTotal = Float.parseFloat(total);

        int total2 = (int) theTotal;
        try {

            String orderId = razorpayService.createOrder(total2, "INR");
            HttpSession session = request.getSession();
            session.setAttribute("orderId", orderId);
            session.setAttribute("total", total2);
            session.setAttribute ( "customerEmail", user.getEmail () );
            session.setAttribute ( "customerName", user.getFullName () );
            session.setAttribute ( "customerPhone", user.getPhoneNumber () );

            return ResponseEntity.ok(orderId);

        } catch (RazorpayException e) {
            // Handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during Razorpay integration");
        }
    }


    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmPayment(@RequestBody String orderId, HttpSession session) {
        try {
            System.out.println (keyId );
            System.out.println ("order"+orderId );
            String  prceAfterDiscount = (String) session.getAttribute("price_after_discount");
            float totalOfferPrice = Integer.parseInt(session.getAttribute("total").toString());
            float total = 0;
            if(prceAfterDiscount != null){
                total = Float.parseFloat ( prceAfterDiscount );
            }else {
                total = Float.parseFloat ( String.valueOf ( totalOfferPrice ) );
            }
            session.setAttribute("total_Price" , total);

            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "success");
            responseJson.put("orderId", orderId);
            responseJson.put("amount", total*100);
            responseJson.put("key", keyId);
            responseJson.put("customerName",session.getAttribute ( "customerName" ).toString());
            responseJson.put("customerEmail",session.getAttribute ( "customerEmail" ).toString());
            responseJson.put("customerPhone",session.getAttribute ( "customerPhone" ).toString());





            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            // Handle exception and return error response
            JSONObject errorJson = new JSONObject();
            errorJson.put("status", "error");
            errorJson.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson.toString());
        }
    }
}

