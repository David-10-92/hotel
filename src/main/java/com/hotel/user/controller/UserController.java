package com.hotel.controller;

import com.hotel.dtos.UserDTO;
import com.hotel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/registerUser")
    public String newUserForm(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult bindingResult,Model model){

        if (bindingResult.hasErrors()) {
            return "registerUser";
        }
        if (userService.isEmailAlreadyInUse(userDTO.getEmail())) {
            model.addAttribute("emailError", "Este correo electrónico ya está registrado. Por favor, elija otro.");
            return "registerUser";
        }
        userService.createUser(userDTO);
        return "login";
    }

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
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
