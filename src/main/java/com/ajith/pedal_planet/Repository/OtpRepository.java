package com.ajith.pedal_planet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.OtpEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Integer>{

	public OtpEntity findByEmail(String email);


    boolean existsByEmail (String email);

    List< OtpEntity> findByCreationTimeBefore (LocalDateTime expirationTime);
}
