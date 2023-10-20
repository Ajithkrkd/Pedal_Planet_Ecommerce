package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.CategoryService;
import com.ajith.pedal_planet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping ( "/admin" )
public class CustomerController {




    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerService customerService;


    @GetMapping ( "/customer" )
    public String getCustomer (Model model) {
        model.addAttribute ( "title", "customer Management" );
        return findPaginated ( 1, 5, model );

    }


    @GetMapping ( "/customer/toggle-status/{id}" )
    public String togglCustomerStatus (@PathVariable ( "id" ) Long id) {
        customerService.toggleCustomerStautsById ( id );
        return "redirect:/admin/customer";

    }

    @GetMapping ( "/customer/pages/{pageNumber}" )
    public String findPaginated (@PathVariable ( value = "pageNumber" ) int pageNumber,
                                 @RequestParam ( value = "size", defaultValue = "5" ) int PageSize,
                                 Model model) {

        Page < Customer > page = customerService.getAllCustomerWithPagination ( pageNumber, PageSize );
        List < Customer > customers = page.getContent ( );
        model.addAttribute ( "currentPage", pageNumber );
        model.addAttribute ( "totalPages", page.getTotalPages ( ) );
        model.addAttribute ( "totalItems", page.getTotalElements ( ) );
        model.addAttribute ( "customers", customers );
        model.addAttribute ( "size", PageSize );
        return "CustomerPages/customer";
    }

    //SEARCH FOR CUSTOMER

    @GetMapping ( "/customer/pages/{pageNumber}/{size}/search-product-result" )
    public String searchCustomers (@PathVariable ( "pageNumber" ) int pageNumber,
                                   @PathVariable ( "size" ) int PageSize,
                                   @RequestParam ( "keyword" ) String keyword,
                                   Model model,
                                   Principal principal) {

        int size = PageSize;
        if ( principal == null ) {
            return "redirect:/signin";
        } else {


            Page < Customer > page = customerService.searchCustomers ( pageNumber, size, keyword );

            List < Customer > customers = page.getContent ( );
            model.addAttribute ( "totalPages", page.getTotalPages ( ) );
            model.addAttribute ( "currentPage", pageNumber );
            model.addAttribute ( "totalItems", page.getTotalElements ( ) );
            model.addAttribute ( "customers", customers );

            return "CustomerPages/result-customer";
        }
    }


}
