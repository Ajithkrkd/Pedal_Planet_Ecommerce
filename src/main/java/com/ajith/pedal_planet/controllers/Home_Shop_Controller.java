package com.ajith.pedal_planet.controllers;

import java.time.LocalDate;
import java.util.List;

import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.BannerService;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.VariantService;
import com.ajith.pedal_planet.serviceImpl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ajith.pedal_planet.serviceImpl.CategoryServiceImpl;
import com.ajith.pedal_planet.serviceImpl.ProductServiceImpl;

@Controller
@RequestMapping("/")
public class Home_Shop_Controller {
	@Autowired
	private CategoryServiceImpl categoryService;

	@Autowired
	private ProductServiceImpl productService;

	@Autowired
	private VariantService variantService;
	@Autowired
	ImageServiceImpl imageService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	CustomerService customerService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model model) {
		long customerCount = customerService.count();

		if (customerCount == 0) {
			// Create an admin user when the table is empty
			Customer admin = new Customer();
			admin.setFullName("Admin");
			admin.setEmail("admin@gmail.com");
			admin.setRole("ROLE_ADMIN");
			admin.setPhoneNumber ("9045678909");
			admin.setJoinDate ( LocalDate.now () );
			admin.setPassword ( passwordEncoder.encode ( "admin" ) );
			customerService.save(admin);

			// You can add a success message to the model if needed
			model.addAttribute("message", "Admin user created.");
		}
	List<Product> product = productService.getAvailableProducts();
 		model.addAttribute ( "ActiveBanner",bannerService.findNonDeletedActiveBanner() );
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

	@PostMapping ("/shopByCategory")
	public String filterByCategory(@RequestParam ("category") int id,
									Model model){
		List<Product> availableProducts = productService.getAvailableProductsByCategory ( id );
		model.addAttribute("categories", categoryService.getAvailableCategory());
		model.addAttribute("availableproducts", availableProducts);
		return "shop";
	}

	@PostMapping ("/shop")
	public String filterProducts(@RequestParam (name = "category", required = false) int selectedCategories,
								 @RequestParam(name = "minPrice", required = false) float minPrice,
								 @RequestParam(name = "maxPrice", required = false) float maxPrice,
								 Model model) {
		List<Product> filteredProducts = productService.getAvailableProductsByCategoryAndPriceRange (selectedCategories, minPrice, maxPrice);
		List< Category > allCategories = categoryService.getAvailableCategory ();
		model.addAttribute("categories", allCategories);
		model.addAttribute("availableproducts", filteredProducts);

		return "shop";
	}



	@PostMapping("/shopSearch")
	public String SearchProductsByAnyThing(@RequestParam ("keyword")String keyword ,Model model){

		List<Product>searchResults = productService.findProductsByKeyword (keyword);
		List< Category > allCategories = categoryService.getAvailableCategory ();
		model.addAttribute("categories", allCategories);
		model.addAttribute("availableproducts", searchResults);
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

		return "userSide/single-product";
	}

	

	

}
