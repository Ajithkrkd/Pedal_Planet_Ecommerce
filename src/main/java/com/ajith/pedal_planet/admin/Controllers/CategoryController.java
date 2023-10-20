package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( "/admin" )
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping ( "/categories" )
    public String getCategory (Model model) {

        return findPaginated ( 1 ,5 ,model ) ;
    }

    @GetMapping ( "/categories/pages/{pageNumber}" )
    public String findPaginated (@PathVariable ( value = "pageNumber" ) int pageNumber,
                                 @RequestParam ( value = "size", defaultValue = "5" ) int PageSize,
                                 Model model) {

        Page < Category > page = categoryService.getAllCategoriesWithPagination ( pageNumber, PageSize );
        List < Category > categories = page.getContent ( );
        model.addAttribute ( "currentPage", pageNumber );
        model.addAttribute ( "totalPages", page.getTotalPages ( ) );
        model.addAttribute ( "totalItems", page.getTotalElements ( ) );
        model.addAttribute ( "categories", categories );
        model.addAttribute ( "size", PageSize );
        return "CategoryPages/categories";
    }

    @GetMapping ( "/categories/add" )
    public String getaddCategory (Model model) {
        model.addAttribute ( "category", new Category ( ) );
        return "CategoryPages/AddCategories";
    }

    @PostMapping ( "/categories/add" )
    public String postaddCategory (@ModelAttribute ( "category" ) Category category) {
        categoryService.AddCategory ( category );
        return "redirect:/admin/categories";
    }


    @GetMapping ( "/category/toggle-list/{id}" )
    public String deleteCategory (@PathVariable ( "id" ) int id) {
        categoryService.toggleTheCategory ( id );
        return "redirect:/admin/categories";

    }

    @GetMapping ( "/categories/update/{id}" )
    public String upadateCategory (@PathVariable ( "id" ) int id, Model model) {
        Optional < Category > category = categoryService.getCategoryByid ( id );
        if ( category.isPresent ( ) ) {
            model.addAttribute ( "category", category );
            return "CategoryPages/AddCategories";
        } else {
            return "/error";
        }
    }


    @GetMapping ( "/categories/pages/{pageNumber}/{size}/search-categories-result" )
    public String searchCustomers (@PathVariable ( "pageNumber" ) int pageNumber,
                                   @PathVariable ( "size" ) int PageSize,
                                   @RequestParam ( "keyword" ) String keyword,
                                   Model model,
                                   Principal principal) {

        int size = PageSize;
        if ( principal == null ) {
            return "redirect:/signin";
        } else {


            Page < Category > page = categoryService.searchCategories ( pageNumber, size, keyword );

            List < Category > categories = page.getContent ( );
            model.addAttribute ( "totalPages", page.getTotalPages ( ) );
            model.addAttribute ( "currentPage", pageNumber );
            model.addAttribute ( "totalItems", page.getTotalElements ( ) );
            model.addAttribute ( "categories", categories );

            return "CategoryPages/result-category";
        }
    }
}
