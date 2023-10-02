package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant,Long> {


    List<Variant> findAllVariantByProduct_IdAndIsAvailableTrue(Long ProductId);


    Optional<Variant> findByVariantNameAndProduct(String variantName, Product product);


    Optional<Variant> findByIdAndVariantName(Long id, String variantName);

    List<Variant> findAllByProduct_Id(Long productId);


    Optional<Variant> findById(Long id);

    Optional<Variant> findByProduct_idAndId(Long productId, Long variantId);
}
