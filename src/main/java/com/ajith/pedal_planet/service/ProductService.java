package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {



	public void addProducts(Product product);



	public void toggleProductListStatus(Long id);

	public Optional<Product> getProductBy_id(Long id);

	public List<Product> getAvailableProducts();

	public List<Product> getAvailableProductsByCategory(int id);


	
	// PAGINATION
	
	public Page<Product> getAllproductWithPagination( int pageNumber , int size);

	public Page<Product> searchProduct(int pageNumber, String keyword);


    Optional<Product> findByName(String name);

    List<Product> getRelatedProductsByCategory(Category category,Long ProductId);

    Long getTotalPriceForOneProduct(Long productId);


    List<Product> findAll();


}
