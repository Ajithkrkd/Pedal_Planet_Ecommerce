package com.ajith.pedal_planet.models;


import com.ajith.pedal_planet.Enums.CouponType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


    @Entity
    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public class Coupon {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        @Column(unique = true, nullable = false)
        @NotBlank(message = "Coupon code is required")
        private String code;
        private int discount;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date expirationPeriod;
        private int couponStock;
        private int maximumDiscountAmount;
        private CouponType type;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;

        @ManyToOne
        @JoinColumn(name = "category_id")
        private Category category;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private Customer customer;

        private boolean isDeleted;

        public boolean isExpired() {
            return (couponStock == 0 || expirationPeriod != null && expirationPeriod.before(new Date()));
        }
    }


