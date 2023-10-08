package com.ajith.pedal_planet.controllers;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ajith.pedal_planet.DTO.CustomerDTO;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.OtpService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Controller
public class LoginController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	AuthenticationSuccessHandler CustomSuccessHandler;

	@Autowired
	private OtpService otpService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private WalletService walletService;
	

	@GetMapping("/signin")
	public String getLoginPage(Model model, @RequestParam(name = "error", defaultValue = " ") String error) {
		model.addAttribute("error", error);
		model.addAttribute("invalid", "invalid");
		model.addAttribute("blocked", "blocked");
		return "login";
	}

	// FOR BINDING THE ALL ERRORS

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

	@GetMapping("/register")
	public String getRegisterPage(@Param("link") String link,
									Model model,HttpSession session) {
		session.setAttribute("link" ,link);
		model.addAttribute("customerDTO", new CustomerDTO());
		return "register";
	}

	@PostMapping("/createUser")
	public String saveUser(@Valid CustomerDTO customerDTO,
						   BindingResult bindingResult,
						   HttpServletRequest request,
						   HttpSession session,
						   Model model,
						   RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {

		// FOR CHECKING THE EMAIL IS ALREADY PRESENT OR NOT
		boolean emailExist = customerService.checkEmail(customerDTO.getEmail());
		if (emailExist) {

			// FOR ADDING NEW ERRORS
			bindingResult.addError(new FieldError("customerDTO", "email", "email id already in use !!"));
		}
		String password = customerDTO.getPassword();

		if (!(password != null && password.equals(customerDTO.getRepeatPassword()))) {
			bindingResult.addError(new FieldError("customerDTO", "repeatPassword", "Password do not match !!"));
		}

		if (bindingResult.hasErrors()) {
			return "register";
		}

		
		else {
			String link = (String) session.getAttribute("link");
			System.out.println(link);

			if(link != null){
				Optional<Customer> customerWithLink = customerService.getByReferralLink(link);
				System.out.println(customerWithLink.get().getFullName() +"----------------------     customer with link 110");

                Customer newCustomer = customerWithLink.get();
                walletService.addAmountToReferredCustomer(newCustomer);
            }
			session.setAttribute("customerDTO", customerDTO);

			otpService.sentOtp(customerDTO, session);

			return "redirect:/otpPage?success";
		}

	}
}
	
