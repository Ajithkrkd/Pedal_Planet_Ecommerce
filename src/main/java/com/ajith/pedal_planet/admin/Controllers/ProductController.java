package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.Repository.CategoryRepository;
import com.ajith.pedal_planet.Repository.ProductRepository;

import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Image;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.service.CategoryService;
import com.ajith.pedal_planet.service.ImageService;
import com.ajith.pedal_planet.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private CategoryRepository categoryRepository;



    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    //PRODUCT SECTION

    @GetMapping("/products")
    public String getProducts(Model model) {
        return findPaginatedProducts(1, 5, model);
    }


    @GetMapping ("/products/pages/{pageNumber}" )
    public String findPaginatedProducts (@PathVariable ( value = "pageNumber" ) int pageNumber,
                                         @RequestParam ( value = "size", defaultValue = "5" ) int PageSize,
                                         Model model) {

        Page < Product > page = productService.getAllProductWithPagination ( pageNumber, PageSize );
        List < Product > products = page.getContent ( );
        model.addAttribute ( "currentPage", pageNumber );
        model.addAttribute ( "totalPages", page.getTotalPages ( ) );
        model.addAttribute ( "totalItems", page.getTotalElements ( ) );
        model.addAttribute ( "products", products );
        model.addAttribute ( "size", PageSize );
        return "ProductPages/products";
    }

    //SEARCH FOR CUSTOMER

    @GetMapping ( "/products/pages/{pageNumber}/{size}/search-product-result" )
    public String searchCustomers (@PathVariable ( "pageNumber" ) int pageNumber,
                                   @PathVariable ( "size" ) int PageSize,
                                   @RequestParam ( "keyword" ) String keyword,
                                   Model model,
                                   Principal principal) {

        int size = PageSize;
        if ( principal == null ) {
            return "redirect:/signin";
        } else {


            Page < Product > page = productService.searchProduct( pageNumber, size, keyword );

            List < Product > products = page.getContent ( );
            model.addAttribute ( "totalPages", page.getTotalPages ( ) );
            model.addAttribute ( "currentPage", pageNumber );
            model.addAttribute ( "totalItems", page.getTotalElements ( ) );
            model.addAttribute ( "products", products );

            return "ProductPages/result-products";
        }
    }



    @GetMapping("/products/add")
    public String getAddProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories" ,categoryRepository.findCategoryByIsAvailableTrue());
        return "ProductPages/Addproducts";
    }

    @PostMapping("/products/save")
    public String postaddproduct(@ModelAttribute("product") Product product ,
                                 BindingResult result,
                                 @RequestParam("images") List<MultipartFile> imageFiles,
                                 Model model)throws IOException {

        Optional<Product> existingProduct = productService.findByName(product.getName());
        if(existingProduct.isPresent()){
            result.rejectValue("name" , "error.name" ,"product already exist");
            model.addAttribute("categories" ,categoryRepository.findCategoryByIsAvailableTrue());
            return "ProductPages/Addproducts";
        }

        Product newProduct = new Product();
        newProduct .setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setCategory(categoryService.getCategoryByid(product.getCategory().getId()).get());
        newProduct.setShortDescription(product.getShortDescription());
        newProduct.setLongDescription(product.getLongDescription());
        newProduct.setPrice(product.getPrice());
        productService.addProducts(product);


        List<Image> images = new ArrayList<>();
        if(!imageFiles.get(0).getOriginalFilename().equals("")) {
            for (MultipartFile image : imageFiles) {
                String fileLocation = productService.handleFileUpload(image);
                Image imageEntity = new Image();
                imageEntity.setImagePath(fileLocation); // SETTING THE IMAGE PATH TO THE IMAGE TABLE
                imageEntity.setProduct(product); //SETTING THE PRODUCT ID INTO THE IMAGE TABLE
                imageEntity = imageService.saveImage(imageEntity);
                images.add(imageEntity);
            }
        }




        return "redirect:/admin/products";
    }




    @GetMapping("/product/details/{id}")
    public String productDetails(@PathVariable ("id") Long id, Model model) {
        Product product = productService.getProductBy_id(id).get(); // Fetch product details from service
        model.addAttribute("product", product);
        return "ProductPages/productDetails";
    }

    @GetMapping("/product/toggle-list/{id}")
    public String toggleProductListStatus(@PathVariable("id") Long id) {
        productService.toggleProductListStatus(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id,Model model){
        Product product = productService.getProductBy_id(id).orElse(null);

        if(product != null){
            model.addAttribute("product" , product);
            model.addAttribute("categories" , categoryRepository.findCategoryByIsAvailableTrue());
            return "ProductPages/update-product";
        }
        return "redirect:/admin/products";
    }



    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute("product") @Valid Product product,
                                BindingResult result,
                                @RequestParam(value = "deletedImages" , required = false)List<String>deletedImages,
                                @RequestParam(value = "newImages",required = false)List<MultipartFile>newImages
            ,Model model) throws IOException{

        if (result.hasErrors()) {
            System.out.println("error");
            return "redirect:/admin/products/update";
        }
        Optional<Product> existingProductOptional  = productRepository.findById(product.getId());
        if(existingProductOptional.isPresent()){
            Product existingProduct = existingProductOptional.get();

            existingProduct.setName(product.getName());
            existingProduct.setShortDescription(product.getShortDescription());
            existingProduct.setLongDescription(product.getLongDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setPrice(product.getPrice());

            productService.addProducts(existingProduct);

        }




        if(deletedImages != null && !deletedImages.isEmpty()){
            for(String imageId  : deletedImages){
                imageService.deleteImageById(Long.valueOf(imageId));
            }
        }

        //HANDLE NEW IMAGE
        if(newImages != null && !newImages.isEmpty())
        {
            for(MultipartFile newImage : newImages){
                String fileLocation = productService.handleFileUpload(newImage);// SAVE NEW IMAGES AND GET  ITS FILE LOCATION
                Image imageEntity = new Image();
                imageEntity.setImagePath(fileLocation);
                imageEntity.setProduct(product);
                imageEntity = imageService.saveImage(imageEntity);
                if(product.getImages() == null){
                    product.setImages(new ArrayList<>());
                }
                product.getImages().add(imageEntity); //ADD THE IMAGE ENTITY TO THE PRODUCT LIST OF IMAGES
            }
        }



        return "redirect:/admin/products";
    }
}
