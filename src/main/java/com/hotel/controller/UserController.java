package com.hotel.controller;

import com.hotel.dtos.UserDTO;
import com.hotel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/new")
    public String newUserForm(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "new";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "new";
        }
        userService.createUser(userDTO);
        return "home";
    }

    @GetMapping("/home")
    public String basic(){
        return "home";
    }

    @PutMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        userService.editUser(id,userDTO);
        return "redirect:/users/";
    }


    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users/";
    }
}
