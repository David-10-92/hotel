package com.hotel.user.service.impl;

import com.hotel.erros.EmailAlreadyInUseException;
import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;
import com.hotel.user.repository.UserRepository;
import com.hotel.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDTO input) {
        if (isEmailAlreadyInUse(input.getEmail())) {
            throw new EmailAlreadyInUseException("Este correo electrónico ya está registrado. Por favor, elija otro.");
        }
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRol(input.getRol());
        return userRepository.save(user);
    }

    @Override
    public UserDTO getCurrentUserDTO() {
        // Obtener la autenticación del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            // Obtener el nombre de usuario del contexto de seguridad
            String username = authentication.getName();

            // Buscar el usuario en la base de datos por nombre de usuario
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Mapear el usuario a un DTO y devolverlo
            return mapUserToDTO(user);
        } else {
            throw new EntityNotFoundException("Usuario no autenticado");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId()); // Incluir el id si es necesario
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRol(user.getRol()); // Considera cómo mapear el rol si es necesario
        // Otros campos que desees mapear del usuario a DTO

        return userDTO;
    }

    @Override
    public Optional<User> editUser(Long id, UserDTO input) {
        UserDTO currentUser = getCurrentUserDTO();
        if(isEmailAlreadyInUse(input.getEmail()) && !currentUser.getEmail().equals(input.getEmail())){
            throw new EmailAlreadyInUseException("Este correo electronico ya esta en uso. Por favor eliga otro");
        }
        return userRepository.findById(currentUser.getId()).map(user -> {
                    user.setUsername(input.getUsername());
                    user.setEmail(input.getEmail());
                if (input.getPassword() != null && !input.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(input.getPassword()));
                }
                    user.setRol(input.getRol());
                    return userRepository.save(user);
                });
    }

    @Override
    public boolean deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> reservationPage = userRepository.findAll(pageable);
        List<User> reservationList = userRepository.findAll();
        return new PageImpl<>(reservationList, pageable, reservationPage.getTotalElements());
    }

}
