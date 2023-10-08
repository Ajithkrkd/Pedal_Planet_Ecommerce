package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.models.Category;
import com.ajith.pedal_planet.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( "/admin" )
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping ( "/categories" )
    public String getCategory (Model model) {
        model.addAttribute ( "categories", categoryService.getAllCategory ( ) );
        return "/CategoryPages/categories";
    }


    @GetMapping ( "/categories/add" )
    public String getaddCategory (Model model) {
        model.addAttribute ( "category", new Category ( ) );
        return "/CategoryPages/AddCategories";
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
            return "/CategoryPages/AddCategories";
        } else {
            return "/error";
        }
    }

    @GetMapping ( "/searchCategories" )
    public String searchCategoryByName (@RequestParam ( "name" ) String name, Model model) {
        List < Category > categories = categoryService.getCategoriesByName ( name );
        model.addAttribute ( "categories", categories );
        return "/CategoryPages/categories";
    }
}
