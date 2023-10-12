package com.ajith.pedal_planet.Repository;

import java.util.List;

import com.ajith.pedal_planet.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ajith.pedal_planet.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	List<Category> findCategoryByIsAvailableTrue();

	@Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    Page< Category> searchCategory (String keyword, Pageable pageable);
}
