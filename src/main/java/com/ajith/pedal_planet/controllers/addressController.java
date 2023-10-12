package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Repository.AddressRepository;
import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.AddressService;
import com.ajith.pedal_planet.service.BasicServices;
import com.ajith.pedal_planet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class addressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BasicServices basicServices;
    @Autowired
    private AddressService addressService;

    @Autowired
    AddressRepository addressRepository;


    @PostMapping ( "/add_address" )
    public String addCustomerAddress (@ModelAttribute Address address,
                                      Model model, RedirectAttributes redirectAttributes) {

        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            address.setCustomer ( existingCustomer );
            addressService.save ( address );

            redirectAttributes.addFlashAttribute ( "message", "your new address added successfully..!" );
            return "redirect:/account";
        }

        return "redirect:/signin";
    }

    @PostMapping ( "/add_address_checkout" )
    public String addCustomerAddress_from_checkout (@ModelAttribute Address address,
                                                    Model model, RedirectAttributes redirectAttributes) {

        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            address.setCustomer ( existingCustomer );
            addressService.save ( address );

            redirectAttributes.addFlashAttribute ( "message", "your new address added successfully..!" );
            return "redirect:/placeOrder";
        }

        return "redirect:/signin";
    }


    @PostMapping ( "/update_address/{id}" )
    public String updateCustomerForm (@ModelAttribute Address newAddress,
                                      @PathVariable ( "id" ) Long address_id,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        System.out.println ( address_id );

        Optional < Customer > customer = customerService.findByUsername ( basicServices.getCurrentUsername ( ) );
        if ( customer.isPresent ( ) ) {
            Customer existingCustomer = customer.get ( );
            System.out.println ( newAddress );


            Optional < Address > address = addressService.findById ( address_id );
            if ( address.isPresent ( ) ) {
                Address existingAddress = getAddress ( newAddress, address );
                newAddress.setCustomer ( existingCustomer );
                addressRepository.save ( existingAddress );
                redirectAttributes.addFlashAttribute ( "message", "address is updated" );
                return "redirect:/account";

            } else {
                redirectAttributes.addFlashAttribute ( "message", "address is not present" );
                return "redirect:/account";
            }
        } else {
            redirectAttributes.addFlashAttribute ( "error", "customer is not available" );
        }
        return "redirect:/account";

    }

    private static Address getAddress (Address newAddress, Optional < Address > address) {
        Address existingAddress = address.get ( );
        existingAddress.setSecondary_number ( newAddress.getSecondary_number ( ) );
        existingAddress.setTown ( newAddress.getTown ( ) );
        existingAddress.setState ( newAddress.getState ( ) );
        existingAddress.setCity ( newAddress.getCity ( ) );
        existingAddress.setArea ( newAddress.getArea ( ) );
        existingAddress.setHouseName ( newAddress.getHouseName ( ) );
        existingAddress.setPin_code ( newAddress.getPin_code ( ) );
        existingAddress.setLandMark ( newAddress.getLandMark ( ) );
        return existingAddress;
    }


    @GetMapping ( "/delete_address/{address_id}" )
    public String updateCustomerAddress (@PathVariable ( "address_id" ) Long address_id,
                                         RedirectAttributes redirectAttributes) {

        System.out.println ( address_id );
        addressService.deleteById ( address_id );
        redirectAttributes.addFlashAttribute ( "message", "your address is deleted" );
        return "redirect:/account";
    }

    @PostMapping("/account/set_default_address/{addressId}")
    @ResponseBody
    public ResponseEntity <String> updateDefaultAddress(@PathVariable ("addressId") Long addressId) {
        Optional < Customer > customer = customerService.findByUsername (basicServices.getCurrentUsername ());
        if(customer.isPresent ()) {
            Customer existingCustomer = customer.get ();
            addressService.customerAddressWantToMakeDefaultAddress(addressId,existingCustomer);
        }

        return null;
    }

}
