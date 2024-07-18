package com.hotel.user.controller;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.errors.EmailAlreadyInUseException;
import com.hotel.user.errors.InvalidUserException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
                             BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            return "registerUser";
        }

        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Te has registrado correctamente.");
        } catch (EmailAlreadyInUseException e) {
            model.addAttribute("emailError", e.getMessage());
            return "registerUser";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado. Por favor, intente nuevamente.");
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/editUser")
    public String showEditProfileForm(Model model, RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = userService.getCurrentUserDTO();
            model.addAttribute("userDTO", userDTO);
            return "editUser";
        } catch (InvalidUserException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado. Por favor, intente nuevamente.");
            return "redirect:/login";
        }
    }

    @PostMapping("/editUser/{id}")
    public String updateProfile(@PathVariable Long id, @Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDTO", userDTO);
            return "editUser";
        }
        try {
            userService.editUser(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
            return "redirect:/home";
        } catch (EmailAlreadyInUseException e) {
            model.addAttribute("emailError", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            return "editUser";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado. Por favor, intente nuevamente.");
            return "redirect:/home";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deleteUser")
    public String showDeleteProfileForm() {
        return "deleteUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser")
    public String deleteProfile(@RequestParam Long id,Model model,RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil eliminado correctamente.");
            return "redirect:/home";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "deleteUser";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado. Por favor, intente nuevamente.");
            return "redirect:/home";
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
        model.addAttribute("size", size);
        return "users";
    }
}
