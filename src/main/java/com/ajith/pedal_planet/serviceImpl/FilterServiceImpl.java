package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.service.FilterService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class FilterServiceImpl implements FilterService {

    @Override
    public boolean isWithinDateRange(String dateRange, long daysDifference) {
        switch (dateRange) {
            case "Last 30 days":
                return daysDifference <= 30;
            case "Last Week":
                return daysDifference <= 7;
            case "Last Month":

                return daysDifference <= 31;
            case "Last Year":

                return daysDifference <= 365;
            default:
                return true;
        }
    }

    @Override
    public long calculateDaysDifference(LocalDate date1, LocalDate date2) {
        return Math.abs( ChronoUnit.DAYS.between(date1, date2));
    }

}
