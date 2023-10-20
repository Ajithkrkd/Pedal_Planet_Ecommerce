package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WishListRepository extends JpaRepository< Wishlist,Long> {

    boolean existsByProduct_Id (Long productId);

    @Query("SELECT COUNT(w) > 0 FROM Wishlist w WHERE w.customer = :customer AND w.product = :product")
    boolean existsByCustomerAndProduct(@Param("customer") Customer customer, @Param("product") Product product);


    @Modifying
    @Query ("DELETE FROM Wishlist w WHERE w.product.id = :productId")
    void deleteByProductId(@Param ("productId") Long productId);

    @Query("SELECT w FROM Wishlist w WHERE w.customer = :customer")
    List<Wishlist> getAllProductsInWishListByCustomer(@Param("customer") Customer customer);
}
