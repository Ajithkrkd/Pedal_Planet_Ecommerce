package com.ajith.pedal_planet.Enums;

import lombok.Getter;

@Getter

public enum Status {

    PENDING,
    CONFIRMED,
    SHIPPING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCEL_REQUEST_SENT,
    CANCELLED,
    RETURN_REQUEST_SENT,
    RETURNED,
    REFUNDED
}
