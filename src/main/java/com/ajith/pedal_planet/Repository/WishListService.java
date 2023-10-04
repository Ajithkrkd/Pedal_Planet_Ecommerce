package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Wishlist;

import java.util.List;

public interface WishListService {
    boolean checkTheProductExistInTheWishList (Long productId);

    void saveTheWishlist (Wishlist wishlist);

    List< Wishlist> getAllProductsInWishList ( );

    Product getProductFromWihListBy (Long productId);

    void removeProductFromWishlist (Long productId);
}
