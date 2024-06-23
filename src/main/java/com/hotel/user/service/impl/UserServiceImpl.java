package com.hotel.user.service.impl;

import com.hotel.erros.EmailAlreadyInUseException;
import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;
import com.hotel.user.repository.UserRepository;
import com.hotel.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public Optional<User> editUser(Long id, UserDTO input) {
        return userRepository.findById(id).map(user -> {
                    user.setUsername(input.getUsername());
                    user.setEmail(input.getEmail());
                    user.setPassword(passwordEncoder.encode(input.getPassword()));
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
}
