package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface VariantService {


     public void increaseStock(Long orderId);

     public void decreaseStock(List < OrderItem > orderId);

    Optional<Variant> findByIdAndVariantName(Long id, String variantName);
    Optional<Variant> findByVariantNameAndProduct(String variantName, Product product);

    Variant save(Variant variant);

    Optional<Variant> findById(Long id);

    List<Variant> findAvailableVariant(Long id);


    boolean toggleStatus(Long variantId);


    Page<Product> getAllproductWithPagination(int pageNumber, int size);

    Page<Product> searchProduct(int pageNumber, String keyword);

    List< Variant> getProductVariants (Long productId);

    List< Variant> getVariantsByProductId (Long productId);

    Optional< Variant> getVariantById (Long variantId);
}

