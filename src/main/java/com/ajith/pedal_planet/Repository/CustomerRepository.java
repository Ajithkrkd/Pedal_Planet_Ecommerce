package com.ajith.pedal_planet.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ajith.pedal_planet.models.Customer;


public interface CustomerRepository  extends JpaRepository<Customer, Long>{
	
	
	public Customer findByEmail(String email);
	public boolean existsByEmail(String email);
	public Optional<Customer> findById(Long id);
	
	@Query("SELECT c FROM Customer c WHERE c.fullName LIKE %:keyword% OR c.email LIKE %:keyword%")
	 Page<Customer> searchCustomers(@Param("keyword")String keyword , Pageable pageable);



	@Query("SELECT c FROM Customer c WHERE c.email = ?1")
	public Optional<Customer> findByEmailWithQuery(String email);


	public Customer findByResetPasswordToken(String token);

    Optional<Customer> findByReferralLink(String link);

	Optional<Customer> findCustomerByReferralLink(String link);

	@Query("SELECT COUNT(c) FROM Customer c")
	 Long countTotalCustomers ( );

	Long countByIsAvailableFalse ( );

	@Query("SELECT COUNT(c) FROM Customer c WHERE c.joinDate >= :oneWeekAgo")
	Long countCustomersJoinedWithinAWeek(LocalDate oneWeekAgo);
}
