package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.AddressRepository;
import com.ajith.pedal_planet.controllers.CustomerAccountController;
import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.AddressService;
import com.ajith.pedal_planet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CustomerService customerService;




    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteById (Long addressId) {
        Optional < Address > address = addressRepository.findById ( addressId );
        if (address.isPresent ()) {
            Address existingAddress = address.get ();
            existingAddress.setDelete ( true );
            addressRepository.save ( existingAddress );
        }

    }


    @Override
    public Optional<Address> findById(Long addressId) {
        return addressRepository.findById(addressId);
    }

    @Override
    public List<Address> getCustomerAllAddress(Customer existingCustomer) {
        Long customer_id = existingCustomer.getId();
        List<Address> addressList = addressRepository.findAllByCustomer_Id(customer_id);
        System.out.println("reached --------------- "+ customer_id);
        return addressList;
    }

    @Override
    public void customerAddressWantToMakeDefaultAddress (Long addressId,Customer existingCustomer) {

        List<Address> addressList = existingCustomer.getAddresses ();
        for(Address address1 : addressList ){
            address1.setIsDefault ( false );
            addressRepository.save ( address1 );
        }

        Optional < Address > address = addressRepository.findById ( addressId );
        if (address.isPresent ()) {
            Address existingAddress = address.get ();
            existingAddress.setIsDefault ( true );
            addressRepository.save ( existingAddress );
        }
    }

    @Override
    public Address getDefualtAddressByCustomer_Id (Long id) {
       return addressRepository.findDefaultAddressByCustomerId ( id );
    }


}
