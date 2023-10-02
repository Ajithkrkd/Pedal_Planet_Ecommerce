package com.ajith.pedal_planet.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.Image;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.VariantService;
import com.ajith.pedal_planet.serviceImpl.ImageServiceImpl;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.serviceImpl.CategoryServiceImpl;
import com.ajith.pedal_planet.serviceImpl.ProductServiceImpl;

@Controller
@RequestMapping("/")
public class HomeController {
	@Autowired
	private CategoryServiceImpl categoryService;

	@Autowired
	private ProductServiceImpl productService;

	@Autowired
	private VariantService variantService;
	@Autowired
	ImageServiceImpl imageService;

	@GetMapping("/")
	public String home(Model model) {
	List<Product> product = productService.getAvailableProducts();
		model.addAttribute("availableproducts",product);
		return "index";
	}


	@GetMapping("/shop")
	public String shop(Model model) {
		List<Product> availableProducts = productService.getAvailableProducts();

		model.addAttribute("categories", categoryService.getAvailableCategory());
		model.addAttribute("availableproducts", availableProducts);
		return "shop";
	}


	@GetMapping("/shop/productByCategory/{id}")
	public String productByCategory(Model model, @PathVariable int id) {

		model.addAttribute("categories", categoryService.getAvailableCategory());
		 java.util.List<Product> availableProducts = productService.getAvailableProductsByCategory(id);
		model.addAttribute("availableproducts", availableProducts);

		return "shop";
	}
	
	@GetMapping("/shop/single-product/{id}")
	public String getSingleProductPage(@PathVariable("id") Long productId , Model model) {
		Product product = productService.getProductBy_id(productId).get();
		List<Variant> variants = variantService.findAvailableVariant(productId);

		List<Product> relatedProducts = productService.getRelatedProductsByCategory(product.getCategory(),productId);
		/*for( Product x : relatedProducts){System.out.println(x.getName());} TESTING*/

		model.addAttribute("variants" , variants);
		model.addAttribute("product" , product);
		model.addAttribute("relatedProducts" , relatedProducts);

		return "/userSide/single-product";
	}

	

	

}
