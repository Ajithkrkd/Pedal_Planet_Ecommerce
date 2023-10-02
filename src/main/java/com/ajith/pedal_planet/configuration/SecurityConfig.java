package com.ajith.pedal_planet.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@EnableWebSecurity
public class  SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	 CustomSuccessHandler customSuccessHandler ;
	@Autowired
	CustomeFailureHandler customeFailureHandler;

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
	
	 @Bean
	    public ServletContextInitializer servletContextInitializer() {
	        return servletContext -> {
	            servletContext.addListener(RequestContextListener.class);
	        };
	 }
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		
		return provider;

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.
		
		
		authorizeRequests(authorize -> authorize
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/**").hasRole("USER")
				.antMatchers("/static/**").permitAll()
				.antMatchers("/**").permitAll())
				.formLogin(formlogin -> formlogin
						.loginPage("/signin")
						 .loginProcessingUrl("/login")
						.usernameParameter("email")
						.failureHandler(customeFailureHandler)
				.successHandler(customSuccessHandler)
				)

				.logout(logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.deleteCookies("JSESSIONID")
						.logoutSuccessUrl("/signin?logout=true"))
				.csrf().disable()
				.exceptionHandling().accessDeniedPage("/error");
	}

}
