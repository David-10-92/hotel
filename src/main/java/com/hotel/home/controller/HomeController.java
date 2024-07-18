package com.hotel.home.controller;

import com.hotel.room.dtos.DateRangeDTO;
import com.hotel.room.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    ImageService imageService;

    @GetMapping
    public String home(Model model){
        List<String> imageNames = imageService.getImageNames();
        model.addAttribute("imageNames", imageNames);
        model.addAttribute("dateRange", new DateRangeDTO());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("tomorrow", LocalDate.now().plusDays(1));
        return "home";
    }
}
