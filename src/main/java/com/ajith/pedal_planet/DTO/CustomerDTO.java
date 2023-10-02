package com.ajith.pedal_planet.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

import lombok.Data;

@Data
public class CustomerDTO {

	private Long id;


	@NotBlank(message ="User's Full Name  cannot be empty!!" )
	@Length(min = 5, max = 50)
	private String fullName;
	@NotBlank(message = "User's phone number cannot be empty!!")
	@Length(min = 10, max = 10)
	 @Pattern(regexp = "^[0-9]+$", message = "Only numbers are allowed")
	private String phoneNumber;

	@NotBlank(message = "User's email cannot be empty!!")
	@Email
	private String email;
	@NotBlank(message = "User's Password cannot be empty!!")
	@Length(min = 8 ,max = 10)
	private String password;
	@NotBlank(message = "re-enter your password!!")
	private String repeatPassword;
	
	private boolean isAvailable = true;
	
	
	public boolean getIsAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

}
