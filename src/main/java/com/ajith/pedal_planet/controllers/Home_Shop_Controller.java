package com.ajith.pedal_planet.controllers;

import java.util.List;

import com.ajith.pedal_planet.models.Banner;
import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.BannerService;
import com.ajith.pedal_planet.service.VariantService;
import com.ajith.pedal_planet.serviceImpl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ajith.pedal_planet.models.Product;
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


	@GetMapping("/")
	public String home(Model model) {
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

		return "/userSide/single-product";
	}

	

	

}
