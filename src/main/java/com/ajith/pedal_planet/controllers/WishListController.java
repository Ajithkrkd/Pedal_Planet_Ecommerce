package com.ajith.pedal_planet.controllers;


import com.ajith.pedal_planet.DTO.WishListResponse;
import com.ajith.pedal_planet.DTO.WishListToCartResponse;
import com.ajith.pedal_planet.Repository.WishListService;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

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

    @Autowired
    private CartService cartService;


    @GetMapping ()
    public String getWishListItem (Model model) {
        Optional < Customer > existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername () );
        if( existingCustomer.isPresent()) {
            List < Wishlist > wishListItems = wishListService.getAllProductsInWishListByCustomer ( existingCustomer.get ());
            model.addAttribute ( "wishListItems", wishListItems );
            return "userSide/wishList";
        }
         return "redirect:/signin";
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


            Optional < Customer > existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
            if ( existingCustomer.isPresent ( ) ) {
                System.out.println ("this is customer " + existingCustomer.get() );
                boolean isProductExistInTheWishList = wishListService.checkProductInCustomerWishlist ( existingCustomer.get ( ), productId );
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

        response.setError ( "user is not there" );
        return ResponseEntity.status ( HttpStatus.BAD_REQUEST ).body ( response );

    }

    @GetMapping ( "/check/{productId}" )
    @ResponseBody
    public ResponseEntity < Map < String, Boolean > > checkWishlist (@PathVariable ( "productId" ) Long productId) {
        Optional<Customer> existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername () );
        Map < String, Boolean > response = new HashMap <> ( );
        boolean inWishlist = false;
        if(existingCustomer.isPresent ()) {
            inWishlist = wishListService.checkProductInCustomerWishlist ( existingCustomer.get (), productId );

            response.put ( "inWishlist", inWishlist );
            return ResponseEntity.ok ( response );
        }
        response.put ( "inWishlist" ,inWishlist);
        return ResponseEntity.ok (response);
    }


    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeProductFromWishlist(@PathVariable Long productId) {
        Optional < Customer > existingCustomer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( existingCustomer.isPresent ( ) ) {
            wishListService.removeProductFromWishlist ( productId );
            return ResponseEntity.ok ( "Product removed from the wishlist." );
        }
        return ResponseEntity.ok ( "" );
    }





        @PostMapping("/findVariants")
        @ResponseBody
        public ResponseEntity < List < String > > getVariantsByProductId(@RequestParam Long productId) {
            List<Variant> variants = variantService.getVariantsByProductId(productId);
            List<String> result = new ArrayList <>();
            for (Variant variant : variants) {
                String variantIdAndName = variant.getId() + "," + variant.getVariantName();
                result.add(variantIdAndName);
            }
            return ResponseEntity.ok(result);
        }

        @PostMapping("/getVariantAndAddProductToCart")
        public String getVariantAndAddProductToCart(@RequestParam ("variantId") String variantId ,
                                                    RedirectAttributes redirectAttributes, Principal principal) {

        System.out.println (variantId );


            if(principal == null){
               return  "redirect:/signin?cart_error";

            }else {
                Optional< Variant > variant = variantService.getVariantById( Long.valueOf ( variantId ) );
                if(variant.isPresent ()){
                    Variant existingVariant = variant.get ();
                    Optional< Customer > customer = (customerService.findByUsername(basicServices.getCurrentUsername()));
                    Optional< Cart > customerCart = customer.map(cartService::getCart).orElse(null);
                    if (existingVariant.getStock() > 0) {
                        CartItem cartItem = new CartItem(existingVariant, 1, customerCart.get());
                        cartService.addToCartList(cartItem);
                        wishListService.removeProductFromWishlist (existingVariant.getProduct ().getId () );
                        redirectAttributes.addFlashAttribute ( "success", "Product added to cart" );
                        return "redirect:/wishList";
                    }else{
                            redirectAttributes.addFlashAttribute ( "error" , "Stock unavailable" );
                        return "redirect:/wishList";

                    }
                }


            }

            return "/redirect:wishList";
        }

}



