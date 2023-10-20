package com.ajith.pedal_planet.controllers;

import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.Product;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.ProductService;
import com.ajith.pedal_planet.service.VariantService;
import com.ajith.pedal_planet.serviceImpl.VariantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class VariantController {

    @Autowired
    private ProductService productService;

    @Autowired
    VariantServiceImpl variantService;




    @GetMapping("/variant")
    public String listProduct(Model model) {
        return findPaginatedProductsForVariant(1,5, model);
    }
    @GetMapping ("/variant/pages/{pageNumber}" )
    public String findPaginatedProductsForVariant (@PathVariable ( value = "pageNumber" ) int pageNumber,
                                                     @RequestParam ( value = "size", defaultValue = "5" ) int PageSize,
                                                     Model model) {

        Page < Product > page = productService.getAllProductWithPagination ( pageNumber, PageSize );
        List < Product > products = page.getContent ( );
        model.addAttribute ( "currentPage", pageNumber );
        model.addAttribute ( "totalPages", page.getTotalPages ( ) );
        model.addAttribute ( "totalItems", page.getTotalElements ( ) );
        model.addAttribute ( "products", products );
        model.addAttribute ( "size", PageSize );
        return "ProductPages/variants";
    }

    //SEARCH FOR CUSTOMER

    @GetMapping ( "/variant/pages/{pageNumber}/{size}/search-variant-result" )
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

            return "ProductPages/result-variant";
        }
    }
    @GetMapping("/variant/{productId}")
    public String getAll(@PathVariable("productId") Long id ,Model model){


        //FOR GETTING THE VARIANTS RELATED TO THE SPECIFIC PRODUCT
        List<Variant> variants = variantService.findAllByProduct_Id(id);

        //FOR GETTING THE PRODUCT
        Optional<Product> product = productService.getProductBy_id(id);
        //FOR GETTING THE PRODUCT NAME I WANT TO SHOW IN THE PAGE
        String productName = product.get().getName();

        model.addAttribute("productName" , productName);
        model.addAttribute("variants" ,variants);
        model.addAttribute("productId" ,id);
        return "ProductPages/variant-management";
    }
    @GetMapping("/variant/create/{productId}")
    public String createNewVariant(@PathVariable("productId") Long id,
                                    Model model){
        Optional<Product> product = productService.getProductBy_id(id);
        String productName = product.get().getName();

        model.addAttribute("productId" ,id);
        model.addAttribute("productName" ,productName);
        model.addAttribute("variant" ,new Variant());
        return "ProductPages/create-variant";
    }
    @PostMapping("/variant/save")
    public String saveVariant(@ModelAttribute Variant variant,
                              @RequestParam(value = "productId") Long id,
                              Model model,
                              BindingResult result){

        Optional<Product> product = productService.getProductBy_id(id);
        String productName = product.get().getName();
        Optional<Variant> existingVariant = variantService.findByVariantNameAndProduct(variant.getVariantName(),product.get());

        if(existingVariant.isPresent()){
            result.rejectValue("variantName","error.variantName" ,"variant Already exist");

            model.addAttribute("productId" ,id);
            model.addAttribute("productName" ,productName);
            model.addAttribute("variant" ,variant);

            return "ProductPages/create-variant";
        }
        variant.setProduct(product.get());
        variantService.save(variant);
        return "redirect:/admin/variant/"+id;
    }






//HERE IAM EDITING THE PARTICULAR VARIANT THEREFORE I WANT GET THE VARIANT BY ID
    //In this controller Iam going to edit the existing variant , variant means a product variant ok!
    // when user click the end point the product id is passed through the end point when I got the product id
    // Using the product id , I found the variant because each variant is related to the product there is foreign key
    //When I got the variant using the variant i will find the product id , product name , variant id!
    @GetMapping("/variant/edit/{id}")
    public String updateVariant(@PathVariable("id") Long id,
                                Model model){

        Optional<Variant> variant = variantService.findById(id);  //this id is product id !
        Long variant_id = variant.get().getId();

        Long product_id = variant.get().getProduct().getId();
        String productName = variant.get().getProduct().getName();
        model.addAttribute("productName" ,productName);
        model.addAttribute("variant" ,variant);
        model.addAttribute("productId" ,product_id);
        model.addAttribute("variantId" , variant_id);
        return"ProductPages/update-variant";
    }


    // here iam going write the function for  update  the variant
    @PostMapping("/variant/update")
    public  String updateVariant(@ModelAttribute("variant") Variant variant,
                                 @RequestParam (value = "productId")Long id,
                                 BindingResult result,Model model){
        Long variantId = variant.getId();;


        Optional<Variant>existingVariant = variantService.findByIdAndVariantName(id , variant.getVariantName());
        if (existingVariant.isPresent()) {


            // Check the ID condition
            if (!existingVariant.get().getId().equals(variantId)) {
                result.rejectValue("variantName", "error.variantName", "Variant already exists for the product");
                model.addAttribute("productName", productService.getProductBy_id(id).get().getName());
                return "ProductPages/update-variant";
            }
        }
        Optional<Product> product = productService.getProductBy_id(id);
        variant.setProduct(product.get());
         variantService.save(variant);
        return "redirect:/admin/variant/"+id;
    }

    //For toggling the availability of variant
    @GetMapping("/variants/toggle-list/{ProductId}/{id}")
    public ResponseEntity<Map<String , Object>> toggleProductStatus(
                                @PathVariable("ProductId") Long productId,
                                @PathVariable("id") Long variantId,
                                RedirectAttributes redirectAttributes){

        Map<String , Object> response = new HashMap<>();
        try{
            boolean isAvailable = variantService.toggleStatus(variantId);
            String message = isAvailable? "variant is now Available" : "variant is now unavailable";

            response.put("success" , true);
            response.put("message" ,message);
            response.put("isAvailable" , isAvailable);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            response.put("success" ,false);
            response.put("message" , "error while toggling the variant");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }
}
