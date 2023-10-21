package com.ajith.pedal_planet.serviceImpl;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ajith.pedal_planet.DTO.CustomerDTO;
import com.ajith.pedal_planet.Repository.CustomerRepository;
import com.ajith.pedal_planet.Repository.OtpRepository;
import com.ajith.pedal_planet.service.OtpService;
import com.ajith.pedal_planet.models.OtpEntity;
@Service
public class OtpServiceImpl implements OtpService {

	@Autowired
	 private  JavaMailSender mailSender;
	
	@Autowired	
	private OtpRepository otpRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	

	@Override
	public int genrateOtp(CustomerDTO customerDto) {
		Random random = new Random();
		int otp = 100_000 + random.nextInt(900_000);
		saveOtp(customerDto, otp);
		return  otp;
	}

	private void saveOtp(CustomerDTO customerDto, int otp) {
		otpRepository.save(buildOtp(customerDto, otp));
	}

	private OtpEntity buildOtp(CustomerDTO customerDto, int otp) {
		OtpEntity otpEntity = new OtpEntity();
		otpEntity.setOtp(otp);
		otpEntity.setEmail(customerDto.getEmail());
		otpEntity.setCreationTime(LocalDateTime.now());
		return otpEntity;
	}

	/**
	 * @param customerDTO
	 * @param session
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public void sentOtp(@ModelAttribute CustomerDTO customerDTO ,HttpSession session) throws MessagingException, UnsupportedEncodingException {
		int otp = genrateOtp(customerDTO);
		System.out.println(otp);
		sentOtpToEmail(customerDTO.getEmail(), otp);
		session.setAttribute("userEmail", customerDTO.getEmail());
		
	}

	@Override
	public void deleteExistingOtp(String email) {

		OtpEntity otp =	otpRepository.findByEmail(email);
		otpRepository.delete(otp);
	}

	/**
	 * @param email
	 * @return
	 */
	@Override
	public boolean isOtpExisitByCustomer (String email) {
		return otpRepository.existsByEmail ( email );
	}

	/**
	 *
	 */
	@Override
	public void removeAllExpiredOtp ( ) {
		LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(4);
		List<OtpEntity> expiredOtps = otpRepository.findByCreationTimeBefore(expirationTime);
		for(OtpEntity otp : expiredOtps){
			otpRepository.delete(otp);
		}
	}


	@Override
	public void sentOtpToEmail(String customerEmail, int otp) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("pedalplanetbicycles@gmail.com" , "Pedal_Planet support");

		helper.setTo(customerEmail);
		helper.setSubject("Hey welcome to 'PEDAL PLANET' stay connected");
		String content = "<html><body style='font-family: Arial, sans-serif;'>"
				+ "<h2 style='color: #007bff;'>Hey there!</h2>"
				+ "<p>We're excited to have you join 'PEDAL PLANET'. To verify your email address, please use the following One-Time Password (OTP):</p>"
				+ "<p style='font-size: 24px; color: #007bff;'>" + otp + "</p>"
				+ "<p>If you didn't request this OTP or have any questions, feel free to contact our support team.</p>"
				+ "<p>Happy pedaling!</p>"
				+ "<p style='color: #888;'>Note: This OTP is valid for a single use and will expire shortly.</p>"
				+ "</body></html>";
		helper.setText(content, true);
		mailSender.send(message);
	}

	

	@Override
	public Integer getStoredOtpByEmail(String email) {
	        OtpEntity otpEntity = otpRepository.findByEmail(email);
	        return (otpEntity != null) ? otpEntity.getOtp() : null;
	    }
	


}
