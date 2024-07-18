package com.hotel.room.service.impl;

import com.hotel.room.errors.ImageNotFoundException;
import com.hotel.room.errors.ImageUploadException;
import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import com.hotel.room.repository.ImageRepository;
import com.hotel.room.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return Optional.ofNullable(imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada")));
    }

    @Override
    public List<Image> createImage(ImageDTO imageDTO, MultipartFile[] files){
        if (files == null || files.length == 0) {
            throw new ImageUploadException("No se ha proporcionado ningún archivo para subir");
        }

        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new ImageUploadException("El archivo de imagenes está vacío: ");
            }

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
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("No se ha proporcionado ningún archivo para subir");
        }

        String imageUrl = "/uploads/" + file.getOriginalFilename();
        return Optional.ofNullable(imageRepository.findById(id).map(image -> {
            image.setImageUrl(imageUrl);
            image.setTypeImage(input.getTypeImage());
            imageRepository.save(image);
            return image;
        }).orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada")));
    }

    @Override
    public boolean deleteImage(Long id) {
        return imageRepository.findById(id).map(image -> {
            imageRepository.delete(image);
            return true;
        }).orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada"));
    }

    @Override
    public List<String> getImageNames() {
        List<String> imageNames = new ArrayList<>();
        try {
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            Files.walk(uploadPath, 1).filter(Files::isRegularFile).forEach(file -> {
                imageNames.add("/uploads/" + file.getFileName().toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageNames;
    }
}
