package com.encapsulearn.quiz_api.config;

import com.encapsulearn.quiz_api.entity.User;
import com.encapsulearn.quiz_api.enums.Role;
import com.encapsulearn.quiz_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialAdminUserConfig {

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create Admin User if not exists
//            if (userRepository.findByUsername("admin").isEmpty()) {
//                User admin = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin123")) // Strong password for admin
//                        .role(Role.ROLE_ADMIN)
//                        .build();
//                userRepository.save(admin);
//                System.out.println("Admin user 'admin' created with password 'adminpass'");
//            }

            // Create Regular User if not exists
//            if (userRepository.findByUsername("user").isEmpty()) {
//                User user = User.builder()
//                        .username("user")
//                        .password(passwordEncoder.encode("user123")) // Strong password for user
//                        .role(Role.ROLE_USER)
//                        .build();
//                userRepository.save(user);
//                System.out.println("User 'user' created with password 'userpass'");
//            }
        };
    }
}
