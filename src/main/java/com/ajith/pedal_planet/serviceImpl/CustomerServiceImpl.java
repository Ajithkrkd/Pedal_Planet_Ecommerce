package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.DTO.CustomerDTO;
import com.ajith.pedal_planet.Enums.Wallet_Method;
import com.ajith.pedal_planet.Exceptions.CustomerNotFoundException;
import com.ajith.pedal_planet.Repository.AddressRepository;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.models.Address;
import com.ajith.pedal_planet.models.Cart;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Wallet;
import com.ajith.pedal_planet.service.CustomerService;
import com.ajith.pedal_planet.service.WalletHistoryService;
import com.ajith.pedal_planet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private WalletHistoryService walletHistoryService;
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	private WalletService walletService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AddressRepository addressRepository;

	@Override
	public Customer createUser(Customer userEntity) {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		userEntity.setRole("ROLE_USER");
		return customerRepository.save(userEntity);
	}

	@Override
	public boolean checkEmail(String email) {
		return customerRepository.existsByEmail(email);
	}

	@Override
	public boolean isValidUser(String email, String password) {
		Customer customer = customerRepository.findByEmail(email);

		if (customer != null) {
			if (passwordEncoder.matches(password, customer.getPassword())) {
				return true;
			}
		}
		return false;
	}



	@Transactional
	public void registerNewCustomer(CustomerDTO customerDTO , HttpServletRequest request ,String link2) {

		Customer customer = new Customer();
		Wallet wallet = new Wallet();
		customer.setEmail(customerDTO.getEmail());
		customer.setFullName(customerDTO.getFullName());
		customer.setJoinDate ( LocalDate.now (  ) );
		customer.setPhoneNumber(customerDTO.getPhoneNumber());
		customer.setRole("ROLE_USER");
		customer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
		customer.setWallet(wallet);
		wallet.setCustomer(customer);
		String link = walletService.generateAndReturnReferralLink();
		customer.setReferralLink(link);

		if(link2 == null){
			walletHistoryService.saveWalletHistory(wallet,customer, Wallet_Method.JOIN_BONUS);
		}else {

			walletHistoryService.saveWalletHistory(wallet,customer, Wallet_Method.FROM_REFERRAL);
		}

		customerRepository.save(customer);


	}


	// ADMIN
	@Override
	public List<Customer> getAllCustomer() {
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customer> getCustomerById(Long id) {
		return customerRepository.findById(id);
	}

	@Override
	public void toggleCustomerStautsById(Long id) {
		Optional<Customer> OptionalCustomer = getCustomerById(id);

		if (OptionalCustomer.isPresent()) {
			System.out.println(OptionalCustomer);
			Customer customer = OptionalCustomer.get();
			customer.setAvailable(!customer.getIsAvailable());
			System.out.println(customer);
			customerRepository.save(customer);

		}

	}

	
// 	PAGINATION
	
	@Override
	public Page<Customer> getAllCustomerWithPagination(int pageNumber, int size) {
		Pageable pageable = PageRequest.of(pageNumber-1, size);
		return this.customerRepository.findAll(pageable);
	}
	
	//SEARCHING

	@Override
	public Page<Customer> searchCustomers(int pageNumber,int size ,String keyword) {
		Pageable pageable = PageRequest.of(pageNumber-1, size);
		Page <Customer> customers = customerRepository.searchCustomers(keyword, pageable);
		return customers;
	}

	@Override
	public Optional<Customer> findByUsername(String email) {
		return Optional.ofNullable(customerRepository.findByEmail(email));
	}

	//forgotten password

	/**
	 * @param token
	 * @param email
	 * @throws CustomerNotFoundException
	 */
	public void updateResetPasswordToken(String token , String email) throws CustomerNotFoundException{
		Optional<Customer> customer = customerRepository.findByEmailWithQuery(email);
		if(customer.isPresent()){
			Customer existingCustomer = customer.get();
			existingCustomer.setResetPasswordToken(token);
			customerRepository.save(existingCustomer);
		}else{
			throw new CustomerNotFoundException("Could not find any customer with the email  " + email );
		}
	}

	public Customer getByResetPasswordToken	(String token){
		return customerRepository.findByResetPasswordToken(token);
	}

	/**
	 * @param customer
	 * @param newPassword
	 */
	public void updatePassword(Customer customer , String newPassword){
		String encodedPassword = passwordEncoder.encode(newPassword);
		customer.setPassword(encodedPassword);
		customer.setResetPasswordToken(null);
		customerRepository.save(customer);
	}

	@Override
	public Optional<Customer>  getByReferralLink(String link) {
		System.out.println("reched service for repository");
		System.out.println(customerRepository.findCustomerByReferralLink(link));
		return customerRepository.findCustomerByReferralLink(link);
	}

	@Override
	public void deleteCart(Cart cart) {
		Customer customer = customerRepository.findById(cart.getCustomer().getId()).orElse(null);
		customer.setCart(null);
		customerRepository.save(customer);
	}

	@Override
	public List < Address > getNonDeltedAddressList (Long id) {
	return 	addressRepository. findByCustomer_IdAndIsDeleteFalse(id);
	}

	@Override
	public Long getTotalNumberOfCustomer ( ) {
		return customerRepository.countTotalCustomers ();
	}

	@Override
	public Long getTotalNumberOfBlockedCustomer ( ) {
		return customerRepository.countByIsAvailableFalse();
	}

	@Override
	public Long getTotalNumberOfRecentCustomer ( ) {
			LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
			return customerRepository.countCustomersJoinedWithinAWeek(oneWeekAgo);

	}

	/**
	 * @param existingCustomer
	 */
	@Override
	public void save (Customer existingCustomer) {
		customerRepository.save ( existingCustomer );
	}

	/**
	 * @return
	 */
	@Override
	public long count ( ) {
		return customerRepository.count ();
	}
}
