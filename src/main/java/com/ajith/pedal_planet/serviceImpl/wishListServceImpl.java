package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.Repository.WishListRepository;
import com.ajith.pedal_planet.Repository.WishListService;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class wishListServceImpl implements WishListService {

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private WishListRepository wishListRepository;


    @Override
    public void saveTheWishlist (Wishlist wishlist) {
        wishListRepository.save ( wishlist );
    }


    @Transactional
    public void removeProductFromWishlist (Long productId) {
        wishListRepository.deleteByProductId ( productId );
    }

    /**
     * @param customer
     * @return
     */
    @Override
    public List < Wishlist > getAllProductsInWishListByCustomer (Customer customer) {
        return wishListRepository.getAllProductsInWishListByCustomer ( customer );
    }

    /**
     * @param customer
     * @param productId
     * @return
     */
    @Override
    public boolean checkProductInCustomerWishlist (Customer customer, Long productId) {
        Optional<Product> existingProduct = productRepository.findById ( productId );
        if ( existingProduct.isPresent()){
            return wishListRepository.existsByCustomerAndProduct ( customer, existingProduct.get() );
        }
        return false;
    }

}
