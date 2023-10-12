package com.ajith.pedal_planet.serviceImpl;

import java.util.List;
import java.util.Optional;

import com.ajith.pedal_planet.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ajith.pedal_planet.Repository.CategoryRepository;
import com.ajith.pedal_planet.service.CategoryService;
import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Product;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	public void AddCategory(Category category) {
		categoryRepository.save(category);
	}



	public Optional<Category> getCategoryByid(int id) {
		return categoryRepository.findById(id);
	}

	public Category getCategoryById(int id) {
		return categoryRepository.findById(id).orElse(null);
	}

	@Override
	public List<Product> getAllProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toggleTheCategory(int id) {
		Optional<Category> optionalCategory = categoryRepository.findById(id);
		if(optionalCategory.isPresent()) {

			Category category = optionalCategory.get();
			System.out.println("optional................"+category);
			category.setAvailable(!category.getIsAvailable());
			categoryRepository.save(category);
		}

	}

	public List<Category> getAvailableCategory() {

		return categoryRepository.findCategoryByIsAvailableTrue();
	}



	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Page < Category > getAllCategoriesWithPagination (int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		return categoryRepository.findAll (pageable);
	}

	@Override
	public Page < Category > searchCategories (int pageNumber, int size, String keyword) {
		Pageable pageable = PageRequest.of(pageNumber-1, size);
		Page <Category> categories = categoryRepository.searchCategory (keyword, pageable);
		return categories;
	}


}
