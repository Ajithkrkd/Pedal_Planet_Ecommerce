package com.ajith.pedal_planet.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.Customer;
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
   private  CustomerRepository customerRepository;

   
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       

        Customer customer = customerRepository.findByEmail(email);
       
        if (customer == null) {
            
        	throw new UsernameNotFoundException("User not available");
        }
        if (!customer.getIsAvailable()) {
            throw new DisabledException("User is blocked");
        }
        
        
        
        else {
        	
        	return new CustomerDetails(customer);
        }

    }


}
