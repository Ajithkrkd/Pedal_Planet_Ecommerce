package com.ajith.pedal_planet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Customer")
public class Customer  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id", nullable = false)
    private Long id;


    @Column(nullable = false)

    private String fullName;


    @Column(name = "email")
    private String email;


    private String phoneNumber;


    private String password;

    private String role;

    private boolean isAvailable = true;

    private LocalDate joinDate;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    private Cart cart;


    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "referralLink")
    private String referralLink;

    @JsonIgnore
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Wallet wallet;

    @JsonIgnore
    @OneToMany(mappedBy ="customer" , cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Address> addresses = new ArrayList<Address>();

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    public boolean getIsAvailable() {

        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {

        this.isAvailable = isAvailable;
    }


}
