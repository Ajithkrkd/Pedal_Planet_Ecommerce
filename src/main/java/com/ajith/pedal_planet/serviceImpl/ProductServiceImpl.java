package com.ajith.pedal_planet.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.service.ProductService;
import com.ajith.pedal_planet.models.Product;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productRepository;

	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}

	public void addProducts(Product product) {
		productRepository.save(product);
	}

	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}






	public void toggleProductListStatus(Long id) {
		Optional<Product> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setAvailable(!product.isAvailable); // Toggle the value
			productRepository.save(product);

		}

	}

	public Optional<Product> getProductBy_id(Long id) {
		return productRepository.findById(id);

	}

	public List<Product> getAvailableProducts() {
		
		return productRepository.findByIsAvailableTrue();
	}

	public List<Product> getAvailableProductsByCategory(int id) {
		return productRepository.findByCategory_IdAndIsAvailableTrue(id);
	}

	//PAGINATION
	



	


	@Override
	public Optional<Product> findByName(String name) {
		return Optional.ofNullable(productRepository.findByName(name));
	}

	@Override
	public List<Product> getRelatedProductsByCategory(Category category ,Long productId) {
		return productRepository.getRelatedProductsByCategory(category ,productId);
	}


	@Override
	public Long getTotalPriceForOneProduct(Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		if(product.isPresent()){
			Product existingProduct = product.get();
			List<Variant> variant = existingProduct.getVariant();
			System.out.println("reached");
			float total_Price = 0;
			for(Variant x : variant){
				total_Price = x.getOfferPrice();
				return (long) total_Price;
			}
		}
		return 0L;
	}

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public String handleFileUpload(MultipartFile file) throws IOException {

		// Define the directory to save the file in
		String rootPath = System.getProperty("user.dir");
		String uploadDir = rootPath + "/Images/product";

		// Create the directory if it doesn't exist
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// Generate a unique file name for the uploaded file
		String fileName = 		file.getOriginalFilename();
		// Save the file to the upload directory
		String filePath = uploadDir + "/" + fileName;
		Path path = Paths.get(filePath);
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		System.gc();

		return fileName;
	}

	@Override
	public Page < Product > getAllProductWithPagination (int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		return  productRepository.findAll (pageable);
	}

	@Override
	public Page < Product > searchProduct (int pageNumber, int size, String keyword) {
		Pageable pageable = PageRequest.of(pageNumber-1, size);
		Page <Product> products = productRepository.searchProducts (keyword, pageable);
		return products;

	}

	@Override
	public List < Product > getAvailableProductsByCategoryAndPriceRange (int categoryId, float minPrice, float maxPrice) {
			return productRepository.getAvailableProductsByCategoryAndPriceRange(categoryId, minPrice, maxPrice);
	}

	@Override
	public List < Product > findProductsByKeyword (String keyword) {
		return productRepository.getProductsByKeyword(keyword);
	}


}
