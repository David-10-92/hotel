package com.hotel.config;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;
import com.hotel.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialDataLoader {

    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadInitialData() {
        return args -> {
            if (!userService.isEmailAlreadyInUse("d_cazalla_b@hotmail.com")) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername("david");
                userDTO.setEmail("d_cazalla_b@hotmail.com");
                userDTO.setPassword(passwordEncoder.encode("123456")); // Encriptar la contraseña
                userDTO.setRol("ROLE_ADMIN");

                userService.createUser(userDTO);
            }
        };
    }
}
