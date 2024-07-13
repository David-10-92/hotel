package com.hotel.user.service.impl;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.errors.EmailAlreadyInUseException;
import com.hotel.user.errors.UnauthorizedException;
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
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRol(input.getRol());
        return userRepository.save(user);
    }

    @Override
    public UserDTO getCurrentUserDTO() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            return mapUserToDTO(user);
        } else {
            throw new UnauthorizedException("Usuario no autenticado");
        }
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return Optional.ofNullable(userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el correo.")));
    }

    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRol(user.getRol());

        return userDTO;
    }

    @Override
    public Optional<User> editUser(Long id, UserDTO input) {
        UserDTO currentUser = getCurrentUserDTO();
        if (isEmailAlreadyInUse(input.getEmail()) && !currentUser.getEmail().equals(input.getEmail())) {
            throw new EmailAlreadyInUseException("Este correo electrónico ya está en uso. Por favor, elija otro.");
        }
        return userRepository.findById(currentUser.getId()).map(user -> {
            user.setEmail(input.getEmail());
            if (input.getPassword() != null && !input.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(input.getPassword()));
            }
            user.setRol(input.getRol());
            return userRepository.save(user);
        });
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        userRepository.delete(user);
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
