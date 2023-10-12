package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.DTO.CouponResponse;
import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.Repository.CouponRepository;
import com.ajith.pedal_planet.Repository.CustomerCouponRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.CartService;
import com.ajith.pedal_planet.service.CouponService;
import com.ajith.pedal_planet.service.CustomerCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

@Autowired
    CouponRepository couponRepository;
@Autowired
CustomerCouponService customerCouponService;

@Autowired
private CartService cartService;

@Autowired
private CustomerCouponRepository customerCouponRepository;

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }

    @Override
    public Page<Coupon> findAllPaged(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }

    @Override
    public Page<Coupon> findByCodeLike(Pageable pageable, String keyword) {
        return couponRepository.findByCodeLike(keyword, pageable);
    }

    @Override
    public Coupon getNewCouponDetailsContainedCoupon(Coupon newcoupon, Optional<Coupon> coupon) {

        Coupon existingCoupon = coupon.get();

        if(existingCoupon.getType() == CouponType.PRODUCT){
            newcoupon.setType(CouponType.PRODUCT);
        }else if(existingCoupon.getType() == CouponType.CATEGORY){
            newcoupon.setType(CouponType.CATEGORY);
        }else if(existingCoupon.getType() == CouponType.GENERAL){
            newcoupon.setType(CouponType.GENERAL);
        }

        existingCoupon.setCode(newcoupon.getCode());
        existingCoupon.setType(newcoupon.getType());
        existingCoupon.setCouponStock(newcoupon.getCouponStock());
        existingCoupon.setExpirationPeriod(newcoupon.getExpirationPeriod());
        existingCoupon.setDiscount(newcoupon.getDiscount());
        existingCoupon.setMaximumDiscountAmount(newcoupon.getMaximumDiscountAmount());
        return existingCoupon;
    }

    @Override
    public List<Coupon> getAllGenaralCouponsUserSide() {
        return couponRepository.findByTypeAndIsDeletedFalse(CouponType.GENERAL);
    }

    @Override
    public void decreaseCouponStock (Coupon coupon ) {
        coupon.setCouponStock(coupon.getCouponStock ()-1);
    }

    @Override
    public void setCouponToCustomer (Coupon coupon, Customer customer) {
        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setCoupon ( coupon );
        customerCoupon.setCustomer ( customer );
        customerCouponRepository.save(customerCoupon);
    }

    @Override
    public void removeCouponFromCustomer (Customer currentCustomer , Coupon coupon) {
        CustomerCoupon customerCoupon = customerCouponRepository.findByCustomerAndCoupon(currentCustomer, coupon);
        customerCoupon.setCustomer ( null );
        customerCoupon.setCoupon ( null );
        customerCouponRepository.save ( customerCoupon );

    }

    /**
     * @param cartItemList
     * @param coupon
     * @param response
     * @param customerCart
     * @param cart
     */
    @Override
    public void addDiscountAmountToPrice (List < CartItem > cartItemList, Optional < Coupon > coupon, CouponResponse response, Optional < Cart > customerCart, Cart cart) {
        int totalOfferPrice = Integer.parseInt ( String.valueOf ( cartService.getTotalOfferPrice ( cartItemList ) ) );
        int discountPrice = totalOfferPrice * coupon.get ( ).getDiscount ( ) / 100;
        int priceAfterDiscount = totalOfferPrice - discountPrice;


        response.setDiscountPercentage ( coupon.get ( ).getDiscount ( ) );
        response.setPrice_after_discount ( priceAfterDiscount );
        response.setDiscount_amount ( discountPrice );
        customerCart.get ( ).setCoupon ( coupon.get ( ) );

        cart.setCoupon_discount_amount ( String.valueOf ( discountPrice ) );
        cart.setTotal_amount_AfterDiscount ( String.valueOf ( priceAfterDiscount ) );
    }

    /**
     * @param customerCart
     * @param coupon
     * @param response
     * @param currentCustomer
     */
    @Override
    public void checkCouponApplicableOrNot (Optional < Cart > customerCart, Optional < Coupon > coupon, CouponResponse response, Optional < Customer > currentCustomer) {
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
                    setCouponToCustomer ( coupon.get ( ), currentCustomer.get () );
                } else if ( categoryMatches ) {
                    response.setCoupon_type ( coupon.get ( ).getType ( ) );
                    response.setCategorySpecific ( true );
                    response.setProductSpecific ( false );
                    response.setCustomerApplied ( true );
                    setCouponToCustomer ( coupon.get ( ), currentCustomer.get () );

                } else if ( categoryMatches == false && productMatches == false ) {
                    response.setCoupon_type ( coupon.get ( ).getType ( ) );
                    response.setCategorySpecific ( false );
                    response.setProductSpecific ( false );
                    response.setCustomerApplied ( true );
                    setCouponToCustomer ( coupon.get ( ), currentCustomer.get () );

                } else {
                    String error_message = "enter valid coupon";
                    response.setError_message ( error_message );
                }
                return productMatches || categoryMatches;
            } );
        }


    /**
     * @param coupon
     * @param response
     * @return
     */
    @Override
    public ResponseEntity < CouponResponse > checkCouponExpired (Optional < Coupon > coupon, CouponResponse response) {
        if ( coupon.get ( ).isExpired ( ) ) {
            response.setValid ( false );
            String error_message = "Sorry Coupon is Expired";
            response.setError_message ( error_message );
            return ResponseEntity.ok ( response );
        }
        return null;
    }


}
