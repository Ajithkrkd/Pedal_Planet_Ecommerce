package com.ajith.pedal_planet.controllers;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ajith.pedal_planet.DTO.CustomerDTO;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.OtpService;

import java.io.UnsupportedEncodingException;

@Controller
public class OtpController {


	@Autowired
	private OtpService otpService;
	
	@Autowired
	private CustomerService customerService;


	@GetMapping("/otpPage")
	public String getOtpPage(@RequestParam (name="error" ,defaultValue = " ") String error ,   Model model) {
		model.addAttribute("error" ,error);
		model.addAttribute("invalid" , "invalid");
		model.addAttribute("resend" , "resend");
		model.addAttribute("success" , "success");
		
		return "otp/otpPage";
	}

	@PostMapping("/otpSubmit")
	public String proccessOtp(HttpServletRequest request, HttpSession session) {
		String enteredOtp = request.getParameter("enteredOtp");
		String userEmail = (String) session.getAttribute("userEmail");
		otpService.removeAllExpiredOtp();
		if (userEmail != null) {
			Integer storedOtp = otpService.getStoredOtpByEmail(userEmail); // Implement this method in your otpService

			if (storedOtp != null && enteredOtp != null && storedOtp.equals(Integer.parseInt(enteredOtp))) {
				
				CustomerDTO customerDTO = (CustomerDTO) session.getAttribute("customerDTO");

				String link = (String) session.getAttribute("link");

				customerService.registerNewCustomer(customerDTO,request ,link );

				return "redirect:/signin?success";
			} 
		}
		 
		return "redirect:/otpPage?error=invalid";
	}


	@GetMapping("/reSendOtp")
	public String resendOtp(HttpServletRequest request, HttpSession session) throws MessagingException, UnsupportedEncodingException {
	    CustomerDTO customerDTO = (CustomerDTO) session.getAttribute("customerDTO");
	    if (customerDTO != null) {
			if(otpService.isOtpExisitByCustomer(customerDTO.getEmail ())){
				otpService.deleteExistingOtp(customerDTO.getEmail());
			}
	        otpService.sentOtp(customerDTO, session);
	        return "redirect:/otpPage?success=resend";
	    }
	    return "redirect:/register"; // Handle the case where customerDTO is not found
	}



}
