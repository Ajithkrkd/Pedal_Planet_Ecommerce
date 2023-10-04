package com.ajith.pedal_planet.controllers;


import com.ajith.pedal_planet.DTO.WIshListCartRequest;
import com.ajith.pedal_planet.DTO.WishListResponse;
import com.ajith.pedal_planet.Repository.WishListService;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.models.Wishlist;
import com.ajith.pedal_planet.service.BasicServices;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.ProductService;
import com.ajith.pedal_planet.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/wishList")
public class WishListController {

    @Autowired
    private WishListService wishListService;
    @Autowired
    private ProductService productService;

    @Autowired
    BasicServices basicServices;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VariantService variantService;


    @GetMapping ()
    public String getWishListItem (Model model) {
        List < Wishlist > wishListItems = wishListService.getAllProductsInWishList ( );
        model.addAttribute ( "wishListItems", wishListItems );
        return "/userSide/wishList";
    }

    @PostMapping ( "/add/{productId}" )
    @ResponseBody
    public ResponseEntity < ? > addProductToWishList (@PathVariable ( "productId" ) Long productId, Principal principal) {
        System.out.println ( productId );
        WishListResponse response = new WishListResponse ( );
        if ( principal == null ) {
            response.setError ( "please Login for add to wishList" );
            return ResponseEntity.status ( HttpStatus.BAD_REQUEST ).body ( response );

        }
        boolean isProductExistInTheWishList = wishListService.checkTheProductExistInTheWishList ( productId );
        System.out.println ( isProductExistInTheWishList );
        if ( isProductExistInTheWishList ) {
            response.setError ( "This product is already in your wishlist" );
            return ResponseEntity.status ( HttpStatus.BAD_REQUEST )
                    .body ( response );
        } else {
            Optional < Product > productWantToAdd = productService.getProductBy_id ( productId );
            Wishlist wishlist = new Wishlist ( );
            wishlist.setProduct ( productWantToAdd.get ( ) );
            wishlist.setCustomer ( customerService.findByUsername ( basicServices.getCurrentUsername ( ) ).get ( ) );
            wishListService.saveTheWishlist ( wishlist );
        }
        response.setMessage ( "product added to to the wishlist" );
        return ResponseEntity.ok ( response );
    }

    @GetMapping ( "/check/{productId}" )
    @ResponseBody
    public ResponseEntity < Map < String, Boolean > > checkWishlist (@PathVariable ( "productId" ) Long productId) {

        boolean inWishlist = wishListService.checkTheProductExistInTheWishList ( productId );
        Map < String, Boolean > response = new HashMap <> ( );
        response.put ( "inWishlist", inWishlist );
        return ResponseEntity.ok ( response );
    }


    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeProductFromWishlist(@PathVariable Long productId) {
        wishListService.removeProductFromWishlist(productId);
        return ResponseEntity.ok("Product removed from the wishlist.");
    }



}



