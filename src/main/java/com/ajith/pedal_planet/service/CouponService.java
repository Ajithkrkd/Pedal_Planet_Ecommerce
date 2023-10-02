package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    Coupon saveCoupon(Coupon coupon);

    void deleteCoupon(Long id);

    List<Coupon> findAll();

    Optional<Coupon> findById(Long id);

    Optional<Coupon> findByCode(String code);


    Page<Coupon> findAllPaged(Pageable pageable);

    Page<Coupon> findByCodeLike(Pageable pageable, String keyword);

    Coupon getNewCouponDetailsContainedCoupon(Coupon newcoupon, Optional<Coupon> coupon);

    public List<Coupon> getAllGenaralCouponsUserSide();

    void decreaseCouponStock (Coupon coupon );

    void setCouponToCustomer(Coupon coupon , Customer customer);

    void removeCouponFromCustomer (Customer currentCustomer ,Coupon coupon);
}
