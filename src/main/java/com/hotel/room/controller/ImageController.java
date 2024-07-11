package com.hotel.room.controller;

import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import com.hotel.room.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/createImage")
    public String createImage(Model model){
        model.addAttribute("imageDTO", new ImageDTO());
        return "createImage";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/createImage")
    public String createImage(@Valid @ModelAttribute("imageDTO") ImageDTO imageDTO,
                              @RequestParam("imageUrl") MultipartFile file,
                              Model model){
        if (imageDTO.getTypeImage() == null || imageDTO.getTypeImage().isEmpty()) {
            model.addAttribute("errorMessage", "Debe seleccionar un tipo de imagen");
            return "createImage";
        }

        imageService.createImage(imageDTO,file);
        return "redirect:/home";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public String listImages(Model model){
        List<Image> imageList = imageService.getImagesAll();
        model.addAttribute("images",imageList);
        return "images";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editImage/{id}")
    public String editImage(@PathVariable Long id, Model model) {
        Optional<Image> image = imageService.getImageId(id);
        model.addAttribute("image", image.get());
        return "editImage";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editImage/{id}")
    public String updateImage(@PathVariable Long id, @ModelAttribute("imageDTO") ImageDTO imageDTO,
                              @RequestParam("imageUrl") MultipartFile file,
                              Model model) {

        if (imageDTO.getTypeImage() == null || imageDTO.getTypeImage().isEmpty()) {
            Optional<Image> image = imageService.getImageId(id);
            image.ifPresent(value -> model.addAttribute("image", value));
            model.addAttribute("errorMessage", "Debe seleccionar un tipo de imagen");
            return "editImage";
        }

        imageService.editImage(id, imageDTO,file);
        return "redirect:/images";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return "redirect:/images";
    }
}
