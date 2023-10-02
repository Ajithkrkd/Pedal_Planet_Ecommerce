package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.models.Image;

public interface ImageService {
    Image saveImage(Image image);

    void deleteImage(Image deletedImage);
    void deleteImageById(Long id);
}
