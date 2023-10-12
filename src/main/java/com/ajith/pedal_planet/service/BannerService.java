package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BannerService {

    public List < Banner > getAllBanners ( );

    public Banner createBanner (Banner banner);

    public void deleteBanner (Long id);


    Banner save (Banner banner);

    String uploadBanner (MultipartFile imageFile) throws IOException;

    Object findAllBanners ( );

    Optional < Banner > getBannerById (Long bannerId);

    void bannerWantToMakeActive (Long bannerId);



    Object findNonDeletedActiveBanner ( );
}
