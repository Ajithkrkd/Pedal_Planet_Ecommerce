package com.ajith.pedal_planet.service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ajith.pedal_planet.DTO.CustomerDTO;

import java.io.UnsupportedEncodingException;

public interface OtpService {

	
	public int genrateOtp(CustomerDTO customerDto);
	
	public void sentOtpToEmail(String customerEmail , int otp) throws MessagingException, UnsupportedEncodingException;

	public Integer getStoredOtpByEmail(String userEmail);

	public void sentOtp(@Valid CustomerDTO customerDTO, HttpSession session) throws MessagingException, UnsupportedEncodingException;


	void deleteExistingOtp(String email);

    boolean isOtpExisitByCustomer (String email);

    void removeAllExpiredOtp ( );
}
