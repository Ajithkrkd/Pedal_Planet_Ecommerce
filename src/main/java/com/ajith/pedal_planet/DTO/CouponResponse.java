package com.ajith.pedal_planet.DTO;


import com.ajith.pedal_planet.Enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private boolean valid;
    private boolean productSpecific;
    private boolean categorySpecific;
    private int discountPercentage;
    private boolean applicable;
    private int price_after_discount;
    private int discount_amount;
    private CouponType coupon_type;
    private String error_message;
    private boolean customerApplied;
}