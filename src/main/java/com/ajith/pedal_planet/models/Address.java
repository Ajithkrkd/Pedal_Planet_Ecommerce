package com.ajith.pedal_planet.models;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     Long id;


    private  String houseName;

    private  String area;

    private String town;

    private String city;

    private String state;


    private String pin_code;

    private String landMark;

    private boolean isDefault;

    private String secondary_number;

    private boolean isDelete;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public void setIsDefault ( boolean isDefault) {
         this.isDefault = isDefault;
    }

    public boolean getIsDefault ( ) {
        return isDefault;
    }
}
