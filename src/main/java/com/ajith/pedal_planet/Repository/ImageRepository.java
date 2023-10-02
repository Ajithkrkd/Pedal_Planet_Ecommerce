package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image ,Long> {
    void deleteById(Long id);
}
