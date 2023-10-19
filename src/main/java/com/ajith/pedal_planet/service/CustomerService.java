package com.ajith.pedal_planet.service;

import java.util.List;
import java.util.Optional;

import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Cart;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ajith.pedal_planet.DTO.CustomerDTO;
import com.ajith.pedal_planet.models.Customer;

import javax.servlet.http.HttpServletRequest;

@Service
public interface CustomerService {


    public Customer createUser (Customer customerEntity);

    public boolean checkEmail (String email);

    public boolean isValidUser (String email, String password);

    public void registerNewCustomer (CustomerDTO customerDTO, HttpServletRequest request, String link);


    //ADMIN

    public List < Customer > getAllCustomer ( );

    public Optional < Customer > getCustomerById (Long id);

    public void toggleCustomerStautsById (Long id);


    public Page < Customer > getAllCustomerWithPagination (int pageNumber, int size);

    public Page < Customer > searchCustomers (int pageNumber, int size, String keyword);


    Optional < Customer > findByUsername (String email);

    //forgot password controller methods
    public void updateResetPasswordToken (String token, String email);

    public Customer getByResetPasswordToken (String token);

    public void updatePassword (Customer customer, String newPassword);

    Optional < Customer > getByReferralLink (String link);

    void deleteCart (Cart cart);

    List < Address > getNonDeltedAddressList (Long id);

    Long getTotalNumberOfCustomer ( );

    Long getTotalNumberOfBlockedCustomer ( );

    Long getTotalNumberOfRecentCustomer ( );

    void save (Customer existingCustomer);

    long count ( );
}
