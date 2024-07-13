package com.hotel.room.controller;

import com.hotel.room.errors.ImageNotFoundException;
import com.hotel.room.errors.ImageUploadException;
import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import com.hotel.room.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
                              @RequestParam("imageUrl") MultipartFile[] file,
                              RedirectAttributes redirectAttributes,
                              Model model){
        if (imageDTO.getTypeImage() == null || imageDTO.getTypeImage().isEmpty()) {
            model.addAttribute("errorMessage", "Debe seleccionar un tipo de imagen");
            return "createImage";
        }

        try {
            imageService.createImage(imageDTO, file);
            redirectAttributes.addFlashAttribute("successMessage", "Imagen(es) creada(s) exitosamente");
            return "redirect:/home";
        } catch (ImageUploadException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "createImage";
        }
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
    public String editImage(@PathVariable Long id, Model model,RedirectAttributes redirectAttributes) {
        try {
            Optional<Image> image = imageService.getImageId(id);
            model.addAttribute("image", image.get());
            return "editImage";
        } catch (ImageNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/images";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editImage/{id}")
    public String updateImage(@PathVariable Long id, @ModelAttribute("imageDTO") ImageDTO imageDTO,
                              @RequestParam("imageUrl") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (imageDTO.getTypeImage() == null || imageDTO.getTypeImage().isEmpty()) {
            model.addAttribute("errorMessage", "Debe seleccionar un tipo de imagen");
            model.addAttribute("image", imageDTO);
            return "editImage";
        }

        try {
            imageService.editImage(id, imageDTO, file);
            redirectAttributes.addFlashAttribute("successMessage", "Imagen actualizada exitosamente");
            return "redirect:/images";
        } catch (ImageNotFoundException | ImageUploadException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("image", imageDTO);
            return "editImage";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        try {
            imageService.deleteImage(id);
            redirectAttributes.addFlashAttribute("successMessage", "Imagen eliminada exitosamente");
            return "redirect:/images";
        } catch (ImageNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/images";
        }
    }
}
