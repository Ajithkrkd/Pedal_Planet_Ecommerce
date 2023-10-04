package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.WishListRepository;
import com.ajith.pedal_planet.Repository.WishListService;
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
    private WishListRepository wishListRepository;
    @Override
    public boolean checkTheProductExistInTheWishList (Long productId) {
       return wishListRepository.existsByProduct_Id ( productId );
    }

    @Override
    public void saveTheWishlist (Wishlist wishlist) {
        wishListRepository.save ( wishlist );
    }

    @Override
    public List < Wishlist > getAllProductsInWishList ( ) {
        return wishListRepository.findAll ();
    }

    @Override
    public Product getProductFromWihListBy (Long productId) {
        return wishListRepository.findByProduct_Id ( productId);
    }

    @Transactional
    public void removeProductFromWishlist(Long productId) {
        wishListRepository.deleteByProductId(productId);
    }

}
