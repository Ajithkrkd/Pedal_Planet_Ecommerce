package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository < Banner, Long> {

    Banner findByActiveTrue ( );

    List< Banner> findAllByActiveTrue ( );

    Object findByActiveTrueAndDeletedFalse ( );
}
