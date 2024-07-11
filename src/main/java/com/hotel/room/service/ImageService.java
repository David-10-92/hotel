package com.hotel.room.service;

import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<Image> getImagesAll();
    Optional<Image> getImageId(Long id);
    Image createImage(ImageDTO input,MultipartFile file);
    Optional<Image> editImage(Long id, ImageDTO input,MultipartFile file);
    boolean deleteImage(Long id);

}
