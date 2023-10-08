package com.ajith.pedal_planet.service;

import java.time.LocalDate;

public interface FilterService {
    boolean isWithinDateRange (String dateRange, long daysDifference);

    long calculateDaysDifference (LocalDate orderedDate, LocalDate currentDate);
}
