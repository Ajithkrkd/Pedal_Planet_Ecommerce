package com.ajith.pedal_planet.admin.Controllers;

import com.ajith.pedal_planet.Enums.CouponType;
import com.ajith.pedal_planet.Repository.CategoryRepository;
import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.CategoryService;
import com.ajith.pedal_planet.service.CouponService;
import com.ajith.pedal_planet.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/coupon")
public class AdminCouponController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/listCoupons")
    public String getAllCoupons(Model model) {

        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);

        coupons.stream()
                .map(Coupon::isExpired)
                .forEach(System.out::println );

        return "couponPages/coupon-management";
    }





    @GetMapping("/create")
    public String createCoupon(Model model) {
        List<Product> productList = productRepository.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("productList", productList);
        model.addAttribute("coupon", new Coupon());
        return "couponPages/create-coupon";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon,
                             BindingResult result,
                             Model model) {
        if (couponService.findByCode(coupon.getCode()).isPresent() && !couponService.findByCode(coupon.getCode()).get().isDeleted()) {
            result.rejectValue("code", "error.coupon", "Coupon code must be unique");
            model.addAttribute("categoryList", productService.findAll());
            model.addAttribute("productList", categoryService.findAll());
            model.addAttribute("coupon", new Coupon());
            return "coupon/create-coupon";
        } else {
            if (coupon.getType() == CouponType.PRODUCT) {
                coupon.setCategory(null);
            } else if (coupon.getType() == CouponType.CATEGORY) {
                coupon.setProduct(null);
            } else {
                coupon.setCategory(null);
                coupon.setProduct(null);
            }
            couponService.saveCoupon(coupon);
            return "redirect:/admin/coupon/listCoupons";
        }
    }


    @PostMapping("/delete/{couponId}")
    public String deleteCoupon(@PathVariable("couponId") Long id
            , Model model, RedirectAttributes redirectAttributes) {
        Optional<Coupon> coupon = couponService.findById(id);
        if (coupon.isPresent()) {
            if (coupon.get().isDeleted()) {
                coupon.get().setDeleted(false);
                redirectAttributes.addFlashAttribute("message", "coupon enabled successfully");
                couponService.saveCoupon(coupon.get());
                return "redirect:/admin/coupon/listCoupons";
            } else {
                coupon.get();
                coupon.get().setDeleted(true);
                redirectAttributes.addFlashAttribute("message", "coupon disabled successfully");
                couponService.saveCoupon(coupon.get());
                return "redirect:/admin/coupon/listCoupons";
            }


        } else {
            redirectAttributes.addFlashAttribute("message", "coupon not found");
            return "redirect:/admin/coupon/listCoupons";
        }
    }




    @PostMapping("/update-coupon/{couponId}")
    public String updateCustomerForm(@ModelAttribute Coupon newcoupon,
                                     @PathVariable("couponId") Long id,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {


        Optional<Coupon> coupon = couponService.findById(id);
        if (coupon.isPresent()) {
            Coupon existingCoupon = couponService.getNewCouponDetailsContainedCoupon(newcoupon, coupon );
            couponService.saveCoupon(existingCoupon);
            redirectAttributes.addFlashAttribute("message", "coupom is updated");
            return "redirect:/admin/coupon/listCoupons";

        } else {
            redirectAttributes.addFlashAttribute("message", "coupom is not present");
            return "redirect:/admin/coupon/listCoupons";
        }
    }




}
