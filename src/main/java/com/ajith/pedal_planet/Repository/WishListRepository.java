package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WishListRepository extends JpaRepository< Wishlist,Long> {

    boolean existsByProduct_Id (Long productId);

    Product findByProduct_Id (Long productId);


    @Modifying
    @Query ("DELETE FROM Wishlist w WHERE w.product.id = :productId")
    void deleteByProductId(@Param ("productId") Long productId);
}
