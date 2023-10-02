package com.ajith.pedal_planet.configuration;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ajith.pedal_planet.controllers.CustomerAccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ajith.pedal_planet.controllers.LoginController;


@Configuration
public class CustomSuccessHandler implements AuthenticationSuccessHandler{



	
	
	
	
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    Set<String> roleSet= AuthorityUtils.authorityListToSet(authentication.getAuthorities());
    if(roleSet.contains("ROLE_ADMIN"))
    {
      response.sendRedirect("/admin/");
    }
    else
    {		//setting session for change the login logout in navbar

    	HttpSession session = request.getSession();
        session.setAttribute("name" ,authentication.getName());
    	session.setAttribute("auth", true);
        response.sendRedirect("/");
    }
  }
}