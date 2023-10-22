package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.service.BasicServices;
import com.ajith.pedal_planet.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private OrderService orderService;


    @Autowired
    private BasicServices basicServices;

    @Autowired
    private final TemplateEngine templateEngine;

    public InvoiceController (TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    @GetMapping("/{orderId}")
    public String getInvoice(@PathVariable("orderId") Long orderId , Model model, HttpSession session) {

        Optional<Order> currentOrder  = orderService.getCurrentOrderUsingOrderId(orderId);
        model.addAttribute("currentOrder", currentOrder.get ());
        model.addAttribute("address", currentOrder.get ().getAddress ());
        model.addAttribute("invoicedate", basicServices.getFormattedDate (LocalDate.now ()));
        model.addAttribute("orderId", orderId);
        List < OrderItem > orderItemList = currentOrder.get ( ).getOrderItems ( );
        model.addAttribute ( "orderItemList", orderItemList);



        model.addAttribute("total_price", currentOrder.get().getTotal ());



    return "userSide/invoice";
}

   @GetMapping("/generatePdf/{orderId}")
    public void generatePDF( @PathVariable("orderId") Long orderId ,HttpServletResponse response ,Model model){
        try{
            ITextRenderer renderer = new ITextRenderer (  );

            String htmlContent = renderThymeleafTemplate (orderId ,model);
            String baseURL ="http://pedalplanet.online";
            renderer.setDocumentFromString(htmlContent, baseURL);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            renderer.layout();
            renderer.createPDF(outputStream);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");

            OutputStream responseOutputStream = response.getOutputStream();
            outputStream.writeTo(responseOutputStream);
            responseOutputStream.close();

        }

        catch (Exception e) {
            e.printStackTrace();
        }
   }


    private String renderThymeleafTemplate(Long orderId , Model model) {
        Optional<Order> currentOrder  = orderService.getCurrentOrderUsingOrderId(orderId);
        model.addAttribute("currentOrder", currentOrder.get ());
        model.addAttribute("address", currentOrder.get ().getAddress ());
        model.addAttribute("invoicedate", basicServices.getFormattedDate (LocalDate.now ()));
        model.addAttribute("orderId", orderId);
        List < OrderItem > orderItemList = currentOrder.get ( ).getOrderItems ( );
        model.addAttribute ( "orderItemList", orderItemList);
        model.addAttribute("total_price", currentOrder.get().getTotal ());
        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("userSide/invoice.html", context);
    }
}
