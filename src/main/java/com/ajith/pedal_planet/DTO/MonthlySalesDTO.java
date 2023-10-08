package com.ajith.pedal_planet.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesDTO {
    private int year;
    private int month;
    private double totalSales;

}
