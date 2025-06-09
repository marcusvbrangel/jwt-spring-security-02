package com.mvbr.jwtspringsecurity02.infrastructure.config;

import com.mvbr.jwtspringsecurity02.domain.Role;
import com.mvbr.jwtspringsecurity02.domain.RoleRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("USER"));
        }
        Role adminRole = roleRepository.findByName("ADMIN").get();
        Role userRole = roleRepository.findByName("USER").get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", encoder.encode("admin"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User("user", encoder.encode("user"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }
    }
}

