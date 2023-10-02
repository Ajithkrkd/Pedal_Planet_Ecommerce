package com.ajith.pedal_planet.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
public class CustomeFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException exception) throws IOException, ServletException ,DisabledException {
	    if (exception instanceof InternalAuthenticationServiceException) {
	    	response.sendRedirect("/signin?error=blocked");

	    } else {
	        response.sendRedirect("/signin?error=invalid");
	    }
	}


}
