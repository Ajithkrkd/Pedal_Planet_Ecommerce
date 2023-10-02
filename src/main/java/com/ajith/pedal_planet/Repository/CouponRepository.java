package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.models.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long> {


    Optional<Coupon> findByCode(String code);


    Page<Coupon> findByCodeLike(String keyword, Pageable pageable);


    List<Coupon>  findByTypeAndIsDeletedFalse(CouponType couponType);
}
