package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.DTO.Utility;
import com.ajith.pedal_planet.Exceptions.CustomerNotFoundException;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.BasicServices;
import com.ajith.pedal_planet.service.CustomerService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Controller
public class ForgottenPasswordController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BasicServices basicServices;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            customerService.updateResetPasswordToken(token, email);
            String resetPasswordLink = "http://pedalplanet.online" + "/reset_password?token=" + token;
            sentEmailwithPasswordLink(email, resetPasswordLink);
            redirectAttributes.addFlashAttribute("forgotmessage", "We have sent a reset password link to your email. Please check.");
            return "redirect:/signin";
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Error while sending email");
        }
        return "redirect:/signin";
    }

    private void sentEmailwithPasswordLink(String email, String resetPasswordLink) throws MessagingException ,UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("pedalplanetbicycles@gmail.com" , "Pedal_Planet support");
        helper.setTo(email);

        String subject = "Here's the link to reset your password";
        String content = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #007bff;'>Reset Your Password</h2>"
                + "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p><a href='" + resetPasswordLink + "' style='background-color: #007bff; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Click here to change your password</a></p>"
                + "<p>If the above link doesn't work, you can copy and paste this URL into your browser:</p>"
                + "<p><a href='" + resetPasswordLink + "'>" + resetPasswordLink + "</a></p>"
                + "<p style='color: #888;'>Ignore this email if you remember your password or if you haven't made this request.</p>"
                + "</body></html>";


        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param("token") String token ,Model model, RedirectAttributes redirectAttributes) {
        Customer customer = customerService.getByResetPasswordToken(token);
        model.addAttribute("token" , token);

        if(customer == null){
            System.out.println ("Customer not found" );
            redirectAttributes.addFlashAttribute("forgotMessage" ,"Invalid Token !! Request One More ");
            return "redirect:/signin";
        }
        else {
            return "reset_password_form";
        }

    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {

    String token = request.getParameter("token");
    String password = request.getParameter("password");
    Customer customer = customerService.getByResetPasswordToken(token);

    if(customer == null){
        System.out.println ("here" );
        redirectAttributes.addFlashAttribute("forgotMessage" , "Invalid Token !! Request One More ");
        return "redirect:/signin";
    }else{
        customerService.updatePassword(customer , password);
        redirectAttributes.addFlashAttribute("forgotMessage" ,"You have successfully changed your password.");
        return "redirect:/signin?changed";
    }
    }


    @PostMapping("/account/forgotten_password")
    public String forgottenPassword(@RequestParam ("currentPassword") String currentPassword,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    RedirectAttributes redirectAttributes) {

        Optional <Customer> customer = customerService.findByUsername(basicServices.getCurrentUsername());
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
}
