package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class BootstrapData {

    @Bean
    CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) roleRepository.save(new Role("ROLE_ADMIN"));
            if (roleRepository.findByName("ROLE_TEACHER").isEmpty()) roleRepository.save(new Role("ROLE_TEACHER"));
            if (roleRepository.findByName("ROLE_USER").isEmpty()) roleRepository.save(new Role("ROLE_USER"));

            if (userRepository.findByUsername("admin@demo").isEmpty()) {
                User admin = new User("admin@demo", passwordEncoder.encode("admin"));
                admin.setRoles(new HashSet<>(roleRepository.findAll()));
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("teacher@demo").isEmpty()) {
                User teacher = new User("teacher@demo", passwordEncoder.encode("teacher"));
                teacher.setEmail("teacher@demo.com");
                teacher.setFullName("Demo Teacher");
                Role teacherRole = roleRepository.findByName("ROLE_TEACHER").orElseThrow();
                teacher.getRoles().add(teacherRole);
                userRepository.save(teacher);
            }

            if (userRepository.findByUsername("student@demo").isEmpty()) {
                User student = new User("student@demo", passwordEncoder.encode("student"));
                student.setEmail("student@demo.com");
                student.setFullName("Demo Student");
                Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
                student.getRoles().add(userRole);
                userRepository.save(student);
            }
        };
    }
}
