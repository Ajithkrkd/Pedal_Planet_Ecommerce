package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.CustomerCouponRepository;
import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.CustomerCoupon;
import com.ajith.pedal_planet.service.CustomerCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomerCouponServiceImpl implements CustomerCouponService {


    @Autowired
    private CustomerCouponRepository customerCouponRepository;


    @Override
    public boolean findCouponIsUsedOrNot (Optional < Coupon > coupon, Customer currentCustomer) {
        if ( coupon.isPresent ( ) ) {
            Coupon existingCoupon = coupon.get ( );
            System.out.println ( existingCoupon + "    hello 7" );

            // Check if a CustomerCoupon entity exists for the given coupon's ID
            boolean isCouponApplied = customerCouponRepository.existsByCustomer_IdAndCoupon_Id ( currentCustomer.getId () ,existingCoupon.getId () );

            if ( isCouponApplied ) {
                System.out.println ( "Coupon is applied" );
                return true;
            }
            return false;
        }
        return false;
    }
}