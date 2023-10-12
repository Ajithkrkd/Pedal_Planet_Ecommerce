package com.ajith.pedal_planet.Repository;

import java.util.List;

import com.ajith.pedal_planet.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ajith.pedal_planet.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByCategory_id(int id);

	List<Product> findByIsAvailableTrue();

	List<Product> findByCategory_IdAndIsAvailableTrue(int id);

	Product findByName(String name);
	@Query("SELECT p FROM Product p WHERE p.shortDescription LIKE %:keyword% OR p.name LIKE %:keyword%")
	Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category = :category AND p.id != :productId")
	List<Product> getRelatedProductsByCategory(@Param("category") Category category ,@Param("productId") Long productId);

	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
			"AND p.price BETWEEN :minPrice AND :maxPrice " +
			"AND p.isAvailable = true")
    List< Product> getAvailableProductsByCategoryAndPriceRange (int categoryId, float minPrice, float maxPrice);



	@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isAvailable = true")
	List<Product> findAvailableProductsByPriceRange(float minPrice, float maxPrice);


	@Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.shortDescription LIKE %:keyword% OR p.longDescription LIKE %:keyword%) "
			)
	List< Product> getProductsByKeyword (String keyword);
}
