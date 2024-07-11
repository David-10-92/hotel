package com.hotel.user.controller;

import com.hotel.erros.EmailAlreadyInUseException;
import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;
import com.hotel.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String showCreateProfileForm(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String createProfile(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult bindingResult,Model model){

        if (bindingResult.hasErrors()) {
            return "registerUser";
        }

        try {
            userService.createUser(userDTO);
        } catch (EmailAlreadyInUseException e) {
            model.addAttribute("emailError", e.getMessage());
            return "registerUser";
        }

        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/editUser")
    public String showEditProfileForm(Model model) {
        UserDTO userDTO = userService.getCurrentUserDTO();
        System.out.println(userDTO);
        model.addAttribute("userDTO", userDTO);
        return "editUser";
    }

    @PostMapping("/editUser/{id}")
    public String updateProfile(@PathVariable Long id, @Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            return "editUser";
        }
        try{
            userService.editUser(id,userDTO);
        }catch (EmailAlreadyInUseException e){
            model.addAttribute("emailError",e.getMessage());
            return "editUser";
        }
        userService.editUser(id, userDTO);
        return "redirect:/home";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deleteUser")
    public String showDeleteProfileForm() {
        return "deleteUser";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser")
    public String deleteProfile(@RequestParam Long id,Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/home";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "deleteUser";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public String searchProfiles(Model model,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "5") int size){
        Page<User> usersPage = userService.getAllUsers(page,size);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());
        return "listUsers";
    }
}
