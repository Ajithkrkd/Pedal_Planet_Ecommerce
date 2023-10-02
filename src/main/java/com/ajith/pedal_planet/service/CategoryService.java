package com.ajith.pedal_planet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Product;

@Service
public interface CategoryService {
	public List<Product> getAllProduct();

	public Optional<Category> getCategoryByid(int id);
	public Category getCategoryById(int id) ;
	public List<Category> getAllCategory();
	public void AddCategory(Category category) ;
	public void toggleTheCategory(int id);

	public List<Category> getCategoriesByName(String name);

	List<Category> findAll();
}

