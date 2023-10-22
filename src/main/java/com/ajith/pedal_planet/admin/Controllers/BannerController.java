package com.ajith.pedal_planet.admin.Controllers;


import com.ajith.pedal_planet.models.Banner;
import com.ajith.pedal_planet.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
public class BannerController {


    @Autowired
    private BannerService bannerService;

    String rootPath = System.getProperty("user.dir");
    String uplodDir = rootPath + "/banner";



    @GetMapping("/admin/banner-Management")
    public String getBannerManagement(Model model){
        model.addAttribute ( "banners" , bannerService.findAllBanners() );
        return "admin/banner-management";
    }
    @GetMapping("/banner")//for image croping
    public String getBannerPage(){
        return "banners";
    }


    @PostMapping ("/upload")
    public String  uplodBanner(@RequestParam("file") MultipartFile imageFile,
                               @RequestParam ("url") String url,
                                RedirectAttributes redirectAttributes ) throws IOException {
        System.out.println (imageFile.getSize () );

        if(!imageFile.getOriginalFilename().equals("")) {
                String fileLocation = bannerService.uploadBanner(imageFile);
                Banner banner = new Banner();
                banner.setImagePath(fileLocation);
                banner.setUrl (url);
                banner = bannerService.save(banner);
                redirectAttributes.addFlashAttribute ( "message" , "banner created successFully" );
            return "redirect:/admin/banner-Management";
        }else {
            System.out.println ("hello5" );
            redirectAttributes.addFlashAttribute ( "error_message" ,"image File is Empty" );
            return "redirect:/admin/banner-Management";
        }

    }

    @PostMapping("/admin/setBannerActive/{bannerId}")
    @ResponseBody
    public ResponseEntity <String> updateDefaultAddress(@PathVariable ("bannerId") Long bannerId) {
            Optional < Banner > banner = bannerService.getBannerById ( bannerId );
            boolean isDeleted;
            if( banner.isPresent ()){
                isDeleted = banner.get ().getDeleted ();
                if(isDeleted){
                    return new ResponseEntity<> ( "error" ,HttpStatus.BAD_REQUEST );
                }else{
                    bannerService.bannerWantToMakeActive(bannerId);
                }

            }

       return new ResponseEntity<String>("success", HttpStatus.ACCEPTED);
    }


    @GetMapping("/admin/banner/delete/{id}")
    public String deleteBannerPermenant(@PathVariable ("id") Long id,
                                        RedirectAttributes redirectAttributes) {
        Optional < Banner > banner = bannerService.getBannerById(id);
        boolean isActive ;
        if(banner.isPresent()){
            isActive = banner.get ().isActive ();
            if(isActive){
                redirectAttributes.addFlashAttribute ( "error_message" ,"Oops !! Active Banner Can't Delete !! " );
                return "redirect:/admin/banner-Management";
            }

            bannerService.deleteBanner(id);
            redirectAttributes.addFlashAttribute ( "message" ,"Banner Availability Changed" );
            return "redirect:/admin/banner-Management" ;
        }else{
            redirectAttributes.addFlashAttribute ( "error_message" ,"There is no Banner with this id !" );
            return "redirect:/admin/banner-Management";
        }
    }

}
