package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.ImageRepository;
import com.ajith.pedal_planet.models.Image;
import com.ajith.pedal_planet.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Override
    public Image saveImage(Image image) {
       return imageRepository.save(image);
    }

    @Override
    public void deleteImage(Image deletedImage) {
        imageRepository.delete(deletedImage);
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.deleteById(id);
    }
}
