package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.DTO.MonthlySalesDTO;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.service.BasicServices;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class SalesReportController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private BasicServices basicServices;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private TemplateEngine templateEngine;
    @GetMapping ("/admin/salesReport")
    public String getSalesReport(Model model) {
        List < Order > orderList = orderService.getAllOrdersWithStatusDelivered ();
        model.addAttribute ( "allOrders" , orderList );

        return "admin/sales-report";
    }
    @GetMapping ("/admin/filter-Sales-Report")
    public String filterSalesReport(@RequestParam ("startDate") String startDate,
                                    @RequestParam ("endDate") String endDate,
                                    Model model){

        model.addAttribute ( "startDate" , startDate );
        model.addAttribute ( "endDate" , endDate );


        LocalDate startDate1 = LocalDate.parse(startDate);
        LocalDate endDate1 = LocalDate.parse(endDate);

        List<Order> orderList = orderService.getSalesBetweenTheOrderDate( startDate1, endDate1);
        List<Order> OrderThatIsDelivered = orderService.getAllOrdersWithStatusDeliveredBetweenTheDate (orderList);
        model.addAttribute ( "allOrders" , OrderThatIsDelivered);
        float TotalAmount = orderService.findTotalSalesAmount(OrderThatIsDelivered);
        model.addAttribute ( "total" , TotalAmount );
        return "admin/sales-report";
    }

    @PostMapping("/admin/generatePdf")
    public void generatePDF(@RequestParam ("startDate") String startDate,
                            @RequestParam ("endDate") String endDate,
                            HttpServletResponse response , Model model){
        try{
            ITextRenderer renderer = new ITextRenderer (  );

            String htmlContent = renderThymeleafTemplate (startDate , endDate ,model);
            String baseURL ="http://pedalplanet.online";
            renderer.setDocumentFromString(htmlContent, baseURL);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            renderer.layout();
            renderer.createPDF(outputStream);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=salesReport.pdf");

            OutputStream responseOutputStream = response.getOutputStream();
            outputStream.writeTo(responseOutputStream);
            responseOutputStream.close();

        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String renderThymeleafTemplate(String startDate,String endDate , Model model) {
        model.addAttribute ( "startDate" , startDate );
        model.addAttribute ( "endDate" , endDate );


        LocalDate startDate1 = LocalDate.parse(startDate);
        LocalDate endDate1 = LocalDate.parse(endDate);

        List<Order> orderList = orderService.getSalesBetweenTheOrderDate( startDate1, endDate1);
        List<Order> OrderThatIsDelivered = orderService.getAllOrdersWithStatusDeliveredBetweenTheDate (orderList);
        model.addAttribute ( "allOrders" , OrderThatIsDelivered);
        float TotalAmount = orderService.findTotalSalesAmount(OrderThatIsDelivered);
        model.addAttribute ( "total" , TotalAmount );

        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("admin/sales-report.html", context);
    }

    //graph
    @GetMapping("/admin/monthlySalesChart")
    @ResponseBody
    public List< MonthlySalesDTO > getMonthlySalesData(@RequestParam ("year") int year){
        return orderService.getMonthlySalesData(year);
    }





//pie chart
    @GetMapping("/admin/payment_data")
    @ResponseBody
    public List<Integer> getPaymentMethodForPieChart() {
        try {
            Map<String, Integer> paymentData = orderService.calculatePaymentMethodPercentages();
            System.out.println (paymentData );
            List<Integer> paymentMethodCounts = new ArrayList <> (paymentData.values());
            System.out.println (paymentMethodCounts + "paymentMethodCounts" );
            return paymentMethodCounts;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }




}



