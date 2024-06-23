package com.hotel.room.controller;

import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @GetMapping("/createRoom")
    public String newRoomForm(Model model){
        model.addAttribute("roomDTO",new RoomDTO());
        return "createRoom";
    }
    @PostMapping("/createRoom")
    public String createRoom(@Valid @ModelAttribute("roomDTO")RoomDTO roomDTO,
                             Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "createRoom";
        }
        roomService.createRoom(roomDTO);
        return "redirect:/users/home";
    }

}
