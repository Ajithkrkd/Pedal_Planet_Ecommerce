package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.BannerRepository;
import com.ajith.pedal_planet.models.Banner;
import com.ajith.pedal_planet.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;



    /**
     * @return
     */
    @Override
    public List < Banner > getAllBanners ( ) {
        return bannerRepository.findAll ();
    }

    /**
     * @param banner
     * @return
     */
    @Override
    public Banner createBanner (Banner banner) {
        return bannerRepository.save ( banner );
    }

    /**
     * @param id
     */
    @Override
    public void deleteBanner (Long id) {
        Optional < Banner > banner = bannerRepository.findById ( id );
        if( banner.isPresent()){
            Banner existingBanner = banner.get ();
            existingBanner.setDeleted ( !existingBanner.getDeleted () );
            bannerRepository.save( existingBanner);
        }
    }

    /**
     * @param banner
     * @return
     */
    @Override
    public Banner save (Banner banner) {
       return bannerRepository.save ( banner );
    }

    /**
     * @param imageFile
     * @return
     */
    @Override
    public String uploadBanner (MultipartFile imageFile) throws IOException {

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/banner";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = 		imageFile.getOriginalFilename();
        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);
        try {
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException ( e );
        }
        System.gc();
        System.out.println ("hellow banner is uploaded" +fileName);
        return fileName;
    }

    /**
     * @return
     */
    @Override
    public Object findAllBanners ( ) {
       return bannerRepository.findAll ();
    }

    /**
     * @param bannerId
     * @return
     */
    @Override
    public Optional < Banner > getBannerById (Long bannerId) {
       return bannerRepository.findById (bannerId);
    }

    /**
     * @param bannerId
     */
    @Override
    public void bannerWantToMakeActive (Long bannerId) {
        List < Banner > allBanners = bannerRepository.findAll ( );
        for (Banner b : allBanners) {
            b.setActive ( false );
            bannerRepository.save ( b );
        }

        Optional < Banner > banner = bannerRepository.findById ( bannerId );
        if ( banner.isPresent ( ) ) {
            Banner existingBanner = banner.get ( );
            existingBanner.setActive ( true );
            bannerRepository.save ( existingBanner );
        }
    }

    /**
     * @return
     */
    @Override
    public Object findNonDeletedActiveBanner ( ) {
        return bannerRepository.findByActiveTrueAndDeletedFalse ( );
    }



    /**
     * @return
     */
}
