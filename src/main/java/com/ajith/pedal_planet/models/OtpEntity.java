package com.ajith.pedal_planet.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer otp;
    private String email;
    private LocalDateTime creationTime;
  

    // Constructors, getters, setters
}
