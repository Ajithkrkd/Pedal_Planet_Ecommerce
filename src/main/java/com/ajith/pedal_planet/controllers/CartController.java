package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.DTO.CouponResponse;
import com.ajith.pedal_planet.DTO.DeleteCartItemRequest;
import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.Repository.CartItemRepository;
import com.ajith.pedal_planet.Repository.CartRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import com.ajith.pedal_planet.serviceImpl.CustomerServiceImpl;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CartController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;


    @Autowired
    private VariantService variantService;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired

    private CouponService couponService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerCouponService customerCouponService;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("selectedVariant") Variant variant,
                            Principal principal
                           ) {
            if(principal == null){
                return "redirect:/signin?cart_error";
            }
                 Optional<Customer> customer = (customerService.findByUsername(getCurrentUsername()));
                 Optional<Cart> customerCart = customer.map(cartService::getCart).orElse(null);
                 Long productId = variant.getProduct().getId();

                    if (variant.getStock() > 0) {
                        CartItem cartItem = new CartItem(variant, 1, customerCart.get());
                        cartService.addToCartList(cartItem);
                        return "redirect:/shop/single-product/" +productId +"?cart_added";
                    } else {

                        return "redirect:/shop/single-product/" +productId +"?cart_no_stock";
                    }
    }

    /**
     * @param model
     * @param principal
     * @return /cartPages/cart
     */
    @GetMapping("/cart")
    public String getCartList(Model model ,Principal principal){

            if(principal == null){
                return "redirect:/signin?cart_error";
            }
            else {
                Optional<Customer> customer = (customerService.findByUsername(getCurrentUsername()));
                Optional<Cart> userCart = customer.flatMap(cartService::getCart);

                List<CartItem> cart = userCart.map(Cart::getCartItems).orElse(Collections.emptyList());
                List<CartItem> filteredCart = cart.stream()
                        .filter(cartItem -> cartItem.getQuantity() != 0)
                        .collect(Collectors.toList());
                Long totalPrice = cartService.getTotalPrice(cart);// iam passing here carItems in that cart
                Long total_offer_Price = cartService.getTotalOfferPrice(cart);// iam passing here carItems in that cart
                List<Coupon> Generalcoupon = couponService.getAllGenaralCouponsUserSide();
                model.addAttribute("availableGeneralCoupon",Generalcoupon);
                model.addAttribute("total_price" , totalPrice);
                model.addAttribute("total_Offer_price" , total_offer_Price);
                model.addAttribute("cart", filteredCart);
                model.addAttribute("name", customer.get().getEmail());
                model.addAttribute("customer", customer.get());


                return "/cartPages/cart";
            }

    }


    @PostMapping("/cart/increase")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> increaseCartItem(@RequestBody String variantId) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            Optional<Variant> variant = variantRepository.findById(Long.valueOf(variantId));
            if (variant.isPresent()) {
                Variant selectedVariant = variant.get();
                if (selectedVariant.getStock() > 0) {
                    int newQuantity = cartService.increaseQuantity(Long.valueOf(variantId));
                    float price = variant.get().getPrice();
                    float offerPrice = variant.get().getOfferPrice();
                    Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
                    Long totalPrice = cartService.getTotalPrice(customer.get().getCart().getCartItems());// iam passing here carItems in that cart
                    Long total_offer_Price = cartService.getTotalOfferPrice(customer.get().getCart().getCartItems());// iam passing here carItems in that cart

                    responseMap.put("total_price" , totalPrice);
                    responseMap.put("total_Offer_price" , total_offer_Price);
                    responseMap.put("newQuantity", newQuantity);
                    responseMap.put("variantId" ,variantId);
                    responseMap.put("price" ,price);
                    responseMap.put("offerPrice" ,offerPrice);
                    responseMap.put("variantStock-" + variant.get().getId() ,variant.get().getStock());

                    return ResponseEntity.ok(responseMap);
                }
            }
            responseMap.put("error", "Unable to increase quantity.");
            return ResponseEntity.badRequest().body(responseMap);
        } catch (Exception e) {
            responseMap.put("error", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }



    @PostMapping("/cart/decrease")
    @ResponseBody
    public ResponseEntity<Map<String ,Object>> decreaseCartItem(@RequestBody String variantId){
        Map<String,Object> responseMap = new HashMap<>();
      try{
          int quantity = cartService.decreaseQuantity(Long.valueOf(variantId));
          Optional<Variant> variant = variantRepository.findById(Long.valueOf(variantId));
          Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
          float price = variant.get().getPrice();
          float offerPrice = variant.get().getOfferPrice();
          Long totalPrice = cartService.getTotalPrice(customer.get().getCart().getCartItems());// iam passing here carItems in that cart
          Long total_offer_Price = cartService.getTotalOfferPrice(customer.get().getCart().getCartItems());// iam passing here carItems in that cart
          responseMap.put("variantStock-" + variant.get().getId() ,variant.get().getStock());
          responseMap.put("total_price" , totalPrice);
          responseMap.put("total_Offer_price" , total_offer_Price);
          responseMap.put("newQuantity" ,quantity);
          responseMap.put("price" ,price);
          responseMap.put("offerPrice" ,offerPrice);


          return ResponseEntity.ok(responseMap);
      } catch (NumberFormatException e) {
          // Handle invalid variantId (e.g., not a valid long)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } catch (Exception e) {
          // Handle other exceptions
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }

    }

    //TODO: display messages is not working

    @PostMapping("/cart/delete")
    public String deleteFromCart(@RequestBody DeleteCartItemRequest request) {

        String variantId = request.getVariantId();
        int quantity = request.getQuantity();

        Optional<Customer> customer = customerService.findByUsername(getCurrentUsername());
        Cart userCart = customer.flatMap(cartService::getCart).orElse(null);

        Long variantUUID = Long.valueOf((variantId));
        Variant variant = variantService.findById(variantUUID).orElse(null);

        assert userCart != null;
        userCart.getCartItems()
                .stream()
                .filter(item -> item.getVariant().getId().equals(variantUUID))
                .findFirst()
                .ifPresent(cartService::removeFromCartList);

        return "redirect:/cart?delete";
    }

    @PostMapping("/cart/apply-coupon")
    @ResponseBody
    public ResponseEntity<?> applyCoupon (@RequestParam(value = "couponCode", required = false) String couponCode
                                          , HttpSession session) {

        System.out.println (couponCode + "    hello 1" );
        CouponResponse response = new CouponResponse();
        if (couponCode.isBlank()) {
            response.setValid(false);
            String error_message = "Enter valid coupon code";
            response.setError_message(error_message);
            System.out.println ( "    hello 2" );
            return ResponseEntity.ok(response);

        } else {


            System.out.println ( couponCode + "    hello 3" );
            Customer currentCustomer = customerService.findByUsername ( getCurrentUsername ( ) ).orElse ( null );
            Optional < Cart > customerCart = cartService.getCart ( currentCustomer );
            Optional < Coupon > coupon = couponService.findByCode ( couponCode );

            boolean isApplied = customerCouponService.findCouponIsUsedOrNot ( coupon, currentCustomer );

            Cart cart;
            if ( !isApplied ) {


                if ( coupon.get ( ).isExpired ( ) ) {
                    System.out.println ( couponCode + "    hello 5" );
                    response.setValid ( false );
                    String error_message = "Sorry Coupon is Expired";
                    response.setError_message ( error_message );
                    return ResponseEntity.ok ( response );
                }

                if ( coupon.isPresent ( ) ) {
                    response.setValid ( true );
                    List < CartItem > cartItemList = customerCart.get ( ).getCartItems ( );
                    cart = customerCart.get ( );
                    int totalOfferPrice = Integer.parseInt ( String.valueOf ( cartService.getTotalOfferPrice ( cartItemList ) ) );
                    int discountPrice = totalOfferPrice * coupon.get ( ).getDiscount ( ) / 100;
                    int priceAfterDiscount = totalOfferPrice - discountPrice;


                    response.setDiscountPercentage ( coupon.get ( ).getDiscount ( ) );
                    response.setPrice_after_discount ( priceAfterDiscount );
                    response.setDiscount_amount ( discountPrice );
                    customerCart.get ( ).setCoupon ( coupon.get ( ) );

                    cart.setCoupon_discount_amount ( String.valueOf ( discountPrice ) );
                    cart.setTotal_amount_AfterDiscount ( String.valueOf ( priceAfterDiscount ) );


                    boolean isApplicable = customerCart.get ( ).getCartItems ( ).stream ( ).anyMatch ( item -> {
                        Product couponProduct = coupon.get ( ).getProduct ( );
                        boolean productMatches = couponProduct != null && item.getVariant ( ).getProduct ( ).getId ( ).equals ( couponProduct.getId ( ) );
                        boolean categoryMatches = false;


                        if ( coupon.get ( ).getCategory ( ) != null ) {
                            categoryMatches = item.getVariant ( ).getProduct ( ).getCategory ( ).equals ( coupon.get ( ).getCategory ( ) );
                        }
                        if ( productMatches ) {
                            response.setCoupon_type ( coupon.get ( ).getType ( ) );
                            response.setProductSpecific ( true );
                            response.setCategorySpecific ( false );
                            response.setCustomerApplied ( true );
                            couponService.setCouponToCustomer ( coupon.get ( ), currentCustomer );
                        } else if ( categoryMatches ) {
                            response.setCoupon_type ( coupon.get ( ).getType ( ) );
                            response.setCategorySpecific ( true );
                            response.setProductSpecific ( false );
                            response.setCustomerApplied ( true );
                            couponService.setCouponToCustomer ( coupon.get ( ), currentCustomer );

                        } else if ( categoryMatches == false && productMatches == false ) {
                            response.setCoupon_type ( coupon.get ( ).getType ( ) );
                            response.setCategorySpecific ( false );
                            response.setProductSpecific ( false );
                            response.setCustomerApplied ( true );
                            couponService.setCouponToCustomer ( coupon.get ( ), currentCustomer );

                        } else {
                            String error_message = "enter valid coupon";
                            response.setError_message ( error_message );
                        }
                        return productMatches || categoryMatches;
                    } );


                } else {
                    response.setValid ( false );
                    return ResponseEntity.ok ( response );
                }

            } else {
                response.setValid ( false );
                String error_message = "Coupon is already applied";
                response.setError_message ( error_message );
                return ResponseEntity.ok ( response );
            }
            System.out.println ( "Customer here " );
            cartRepository.save ( cart );
            customerRepository.save ( currentCustomer );
            return ResponseEntity.ok ( response );
        }
    }

    @PostMapping ( "/cart/delete_coupon" )
    public String deleteCoupon ( ) {
        Customer currentCustomer = customerService.findByUsername ( getCurrentUsername ( ) ).orElse ( null );
        Optional < Cart > customerCart = cartService.getCart ( currentCustomer );
        Coupon existingCoupon = customerCart.get ( ).getCoupon ( );
        customerCart.get ( ).setCoupon ( null );
        customerCart.get ( ).setCoupon_discount_amount ( null );
        customerCart.get ( ).setTotal_amount_AfterDiscount ( null );
        couponService.removeCouponFromCustomer(currentCustomer,existingCoupon);
        customerRepository.save ( currentCustomer );
        cartRepository.save ( customerCart.get ( ) );
        return "redirect:/cart?delete";

    }

}
