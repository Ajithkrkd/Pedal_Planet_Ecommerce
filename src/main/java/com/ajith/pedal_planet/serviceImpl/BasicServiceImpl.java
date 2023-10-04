package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.service.BasicServices;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Service
public class BasicServiceImpl implements BasicServices {
    @Override
    public String getFormattedDate (LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMMM d", Locale.ENGLISH);
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        return authentication.getName();
    }
}
