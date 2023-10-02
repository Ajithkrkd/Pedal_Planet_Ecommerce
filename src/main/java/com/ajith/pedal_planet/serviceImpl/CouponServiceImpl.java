package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.Repository.CouponRepository;
import com.ajith.pedal_planet.Repository.CustomerCouponRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.Coupon;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.CustomerCoupon;
import com.ajith.pedal_planet.service.CouponService;
import com.ajith.pedal_planet.service.CustomerCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


}
