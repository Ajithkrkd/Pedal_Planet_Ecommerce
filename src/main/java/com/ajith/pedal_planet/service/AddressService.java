package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Customer;

import java.util.List;
import java.util.Optional;

public interface AddressService {
   /* public Address bindAddress(HttpServletRequest request);
*/
    Address save(Address address);

    void deleteById(Long addressId);

    Optional<Address> findById(Long addressId);

    List<Address> getCustomerAllAddress(Customer existingCustomer);
}
