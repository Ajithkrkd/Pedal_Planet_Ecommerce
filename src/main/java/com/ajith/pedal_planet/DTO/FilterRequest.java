package com.ajith.pedal_planet.DTO;

import com.ajith.pedal_planet.Enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterRequest {
    private Status status;
    private String  time;


}
