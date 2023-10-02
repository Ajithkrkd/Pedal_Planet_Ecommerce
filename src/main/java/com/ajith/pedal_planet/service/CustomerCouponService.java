package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;

import java.util.Optional;

public interface CustomerCouponService {


    boolean findCouponIsUsedOrNot (Optional< Coupon> coupon, Customer currentCustomer);
}
