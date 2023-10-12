package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.CustomerCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon ,Long> {





    CustomerCoupon findByCustomerAndCoupon (Customer currentCustomer, Coupon coupon);







    boolean existsByCustomer_IdAndCoupon_Id (Long id, Long id1);
}
