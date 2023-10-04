package com.ajith.pedal_planet.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;


public interface BasicServices {

      String getFormattedDate (LocalDate date ) ;
      public String getCurrentUsername() ;

}
