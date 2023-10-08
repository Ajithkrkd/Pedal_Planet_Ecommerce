package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.service.ProductService;
import com.ajith.pedal_planet.service.VariantService;
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
@RequestMapping("/admin")
public class PaginationController {

	@Autowired
	private CustomerService customerService;

	
	@Autowired
	private ProductService productService;

	@Autowired
	private VariantService variantService;

	 

	
	// PAGINATION CONTROLLER FOR PRODUCTS
	
	@GetMapping("/products/pages/{pageNumber}")
	public String findPaginatedProducts(@PathVariable (value = "pageNumber") int pageNumber ,
										Model model) {
		int  size = 5;
		Page<Product> page = productService.getAllproductWithPagination(pageNumber, size);
		List<Product> products = page.getContent();
		model.addAttribute("size" , products.size());
		model.addAttribute("currentPage" , pageNumber);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("products" , products);
		return "/ProductPages/products";
		
	}
	@GetMapping("/variant/pages/{pageNumber}")
	public String findPaginatedProductsForVariant(@PathVariable (value = "pageNumber") int pageNumber ,
										Model model) {
		int  size = 5;
		Page<Product> page = variantService.getAllproductWithPagination(pageNumber, size);
		List<Product> products = page.getContent();
		model.addAttribute("size" , products.size());
		model.addAttribute("currentPage" , pageNumber);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("products" , products);
		return "/ProductPages/variants";

	}
	@GetMapping("/products/pages/{pageNumber}/search-result")
	public String searchProducts(@PathVariable("pageNumber")int pageNumber ,
								 @RequestParam("keyword")String keyword,
								 Model model,
								 Principal principal) {
		if(principal == null) {
			return "redirect:/signin";
		}
		else {
			Page<Product> page = productService.searchProduct(pageNumber, keyword);
			List<Product> products = page.getContent();
			model.addAttribute("totalPages" ,page.getTotalPages());
			model.addAttribute("currentPage" ,pageNumber);
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("products" , products);
			return "/Productpages/result-variant.html";
		}
	}
	
}
