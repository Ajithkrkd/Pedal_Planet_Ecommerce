package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class VariantServiceImpl implements VariantService {

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public void increaseStock(Long orderId) {
       List<OrderItem> orderItems =  orderItemRepository.findAllByOrder_Id(orderId);

       for(OrderItem x : orderItems){
           Variant variant = x.getVariant();
           System.out.println (x.getQuantity ()+"ajith quantity set" );
           System.out.println (x.getQuantity ()+"ajith quantity set" );
           variant.setStock(x.getQuantity() + variant.getStock ());
           variantRepository.save(variant);
       }
    }

    @Override
    public void decreaseStock (List < OrderItem > orderItems) {
        for(OrderItem x : orderItems){
            Variant variant = x.getVariant();
            variant.setStock(variant.getStock () - x.getQuantity());
            variantRepository.save(variant);
        }
    }


    @Override
    public Optional<Variant> findByIdAndVariantName(Long id, String variantName) {
        return variantRepository.findByIdAndVariantName(id,variantName);
    }

    @Override
    public Optional<Variant> findByVariantNameAndProduct(String variantName, Product product) {
        return variantRepository.findByVariantNameAndProduct(variantName,product);
    }

    @Override
    public Variant save(Variant variant) {
        return variantRepository.save(variant);
    }

    @Override
    public Optional<Variant> findById(Long id) {
        return variantRepository.findById(id);
    }

    @Override
    public List<Variant> findAvailableVariant(Long id) {
        return variantRepository.findAllVariantByProduct_IdAndIsAvailableTrue(id);
    }


    public List<Variant> findAllByProduct_Id(Long id) {
     return   variantRepository.findAllByProduct_Id(id);
    }

    @Override
    public boolean toggleStatus(Long variantId) {

       Optional<Variant>  variants = variantRepository.findById(variantId);

        if(variants.isPresent())
        {
            Variant variant = variants.get();
            variant.setAvailable(!variant.getIsAvailable());
            variantRepository.save(variant);
            return variant.getIsAvailable();
        }
        else{
            throw new EntityNotFoundException("Variant not found with ID: " + variantId);
        }

    }



    @Override
    public Page<Product> getAllproductWithPagination(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchProduct(int pageNumber, String keyword ) {
        Pageable pageable =PageRequest.of(pageNumber, 5);
        Page<Product> products = productRepository.searchProducts(keyword,pageable);
        return products;
    }

    @Override
    public List < Variant > getProductVariants (Long productId) {
        return variantRepository.findAllVariantByProduct_IdAndIsAvailableTrue ( productId );
    }

    @Override
    public List < Variant > getVariantsByProductId (Long productId) {
      return   variantRepository.findAllVariantByProduct_IdAndIsAvailableTrue ( productId );
    }

    @Override
    public Optional < Variant > getVariantById (Long variantId) {
        return variantRepository.findById ( variantId );
    }


}

