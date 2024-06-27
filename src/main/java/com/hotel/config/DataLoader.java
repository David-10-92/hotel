package com.hotel.config;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(!userService.isEmailAlreadyInUse("d_cazalla_b@hotmail.com")){
            // Crear usuario administrador
            UserDTO adminDTO = new UserDTO();
            adminDTO.setUsername("david");
            adminDTO.setEmail("d_cazalla_b@hotmail.com");
            adminDTO.setPassword("123456"); // Encriptar la contraseña
            adminDTO.setRol("ROLE_ADMIN");

            userService.createUser(adminDTO);
        }
    }
}
