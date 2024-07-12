package com.hotel.room.service.impl;

import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import com.hotel.room.repository.ImageRepository;
import com.hotel.room.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    ImageRepository imageRepository;
    @Override
    public List<Image> getImagesAll() {
        return imageRepository.findAll();
    }

    @Override
    public Optional<Image> getImageId(Long id) {
        return imageRepository.findById(id);
    }

    @Override
    public List<Image> createImage(ImageDTO imageDTO, MultipartFile[] files){
        List<Image> images = new ArrayList<>();
        for(MultipartFile file :  files){
            Image image = new Image();
            String fileUrl = "/uploads/" + file.getOriginalFilename();
            image.setImageUrl(fileUrl);
            image.setTypeImage(imageDTO.getTypeImage());
            imageRepository.save(image);
            images.add(image);
        }
        return images;
    }

    @Override
    public Optional<Image> editImage(Long id, ImageDTO input,MultipartFile file) {
        String imageUrl = "/uploads/" + file.getOriginalFilename();
        return imageRepository.findById(id).map(image -> {
            image.setImageUrl(imageUrl);
            image.setTypeImage(input.getTypeImage());
            imageRepository.save(image);
            return image;
        });
    }

    @Override
    public boolean deleteImage(Long id) {
        return imageRepository.findById(id).map(image -> {
            imageRepository.delete(image);
            return true;
        }).orElse(false);
    }
}
