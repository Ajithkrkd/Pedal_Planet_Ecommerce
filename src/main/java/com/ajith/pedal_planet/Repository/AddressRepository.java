package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository < Address, Long > {

    List < Address > findAllByCustomer_Id (Long customerId);

    List < Address > findByCustomer_IdAndIsDeleteFalse (Long id);


    @Query ( "SELECT a FROM Address a WHERE a.customer.id = :customerId AND a.isDefault = true" )
    Address findDefaultAddressByCustomerId (Long customerId);
}
