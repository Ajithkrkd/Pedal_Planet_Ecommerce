package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {



	public void addProducts(Product product);



	public void toggleProductListStatus(Long id);

	public Optional<Product> getProductBy_id(Long id);

	public List<Product> getAvailableProducts();

	public List<Product> getAvailableProductsByCategory(int id);



	Optional<Product> findByName(String name);

    List<Product> getRelatedProductsByCategory(Category category,Long ProductId);

    Long getTotalPriceForOneProduct(Long productId);


    List<Product> findAll();


    String handleFileUpload (MultipartFile image) throws IOException;

    Page< Product> getAllProductWithPagination (int pageNumber, int pageSize);

	Page< Product> searchProduct (int pageNumber, int size, String keyword);

    List< Product> getAvailableProductsByCategoryAndPriceRange (int categoryId, float minPrice, float maxPrice);

	List< Product> findProductsByKeyword (String keyword);
}
