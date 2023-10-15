package com.ajith.pedal_planet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/contactUS")
    public String getContactPage(Model model) {
        return "contactUS";
    }

    @PostMapping("/sentMail")
    public String sendSupportMail(Model model, RedirectAttributes redirectAttributes,
                                  @RequestParam("CustomerSubject") String CustomerSubject,
                                  @RequestParam("CustomerEmail") String CustomerEmail,
                                  @RequestParam("CustomerName") String CustomerName,
                                  @RequestParam("CustomerMessage") String CustomerMessage) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(CustomerEmail);
        helper.setTo("pedalplanetbicycles@gmail.com");
        helper.setSubject(CustomerSubject);

        // Create a simple HTML email content
        String htmlContent = "<html><body>";
        htmlContent += "<h1>Contact Form Submission</h1>";
        htmlContent += "<p><strong>Name:</strong> " + CustomerName + "</p>";
        htmlContent += "<p><strong>Email:</strong> " + CustomerEmail + "</p>";
        htmlContent += "<p><strong>Subject:</strong> " + CustomerSubject + "</p>";
        htmlContent += "<p><strong>Message:</strong> " + CustomerMessage + "</p>";
        htmlContent += "</body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
        redirectAttributes.addFlashAttribute ("message", "Response Sent Successfully");
        return "redirect:/contactUS";
    }
}



