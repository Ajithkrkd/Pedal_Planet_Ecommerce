package com.ajith.pedal_planet.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ajith.pedal_planet.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	List<Category> findCategoryByIsAvailableTrue();

	@Query("SELECT u FROM Category u WHERE u.name LIKE :name%")
	List<Category> findByName(String name);

}
