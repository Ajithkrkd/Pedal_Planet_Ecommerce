package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Wishlist;

import java.util.List;

public interface WishListService {


    void saveTheWishlist (Wishlist wishlist);


    void removeProductFromWishlist (Long productId);

    List< Wishlist> getAllProductsInWishListByCustomer (Customer customer);

    boolean checkProductInCustomerWishlist (Customer customer, Long productId);
}
