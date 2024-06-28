package com.hotel.room.service;

import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<Image> getImagesAll();
    Optional<Image> getImageId(Long id);
    Image createImage(ImageDTO input);
    Optional<Image> editImage(Long id, ImageDTO input);
    boolean deleteImage(Long id);

}
