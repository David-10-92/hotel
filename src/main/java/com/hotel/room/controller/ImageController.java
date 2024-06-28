package com.hotel.room.controller;

import com.hotel.room.dtos.ImageDTO;
import com.hotel.room.model.Image;
import com.hotel.room.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/createImage")
    public String createImage(Model model){
        model.addAttribute("imageDTO", new ImageDTO());
        return "createImage";
    }

    @PostMapping("/createImage")
    public String createRoom(@Valid @ModelAttribute("imageDTO") ImageDTO imageDTO,
                             Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "createImage";
        }
        imageService.createImage(imageDTO);
        return "redirect:/users/home";
    }

    @GetMapping("/listImages")
    public String listImages(Model model){
        List<Image> imageList = imageService.getImagesAll();
        model.addAttribute("images",imageList);
        return "listImages";
    }

    @GetMapping("/editImage/{id}")
    public String editImage(@PathVariable Long id, Model model) {
        Optional<Image> image = imageService.getImageId(id);
        model.addAttribute("image", image.get());
        return "editImage";  // Nombre de la plantilla para editar la imagen
    }

    // Actualizar imagen
    @PostMapping("/editImage/{id}")
    public String updateImage(@PathVariable Long id, @ModelAttribute ImageDTO imageDTO) {
        imageService.editImage(id, imageDTO);
        return "redirect:/images/listImages";
    }

    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return "redirect:/images/listImages";
    }
}
