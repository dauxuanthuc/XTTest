package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.payload.UpdateProfileRequest;
import com.example.demo.payload.UserProfileDTO;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/hello")
    public String helloUser() {
        return "Hello User - secured";
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        boolean changed = false;
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
            changed = true;
        }
        if (req.getFullName() != null) {
            user.setFullName(req.getFullName());
            changed = true;
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            changed = true;
        }

        if (changed) userRepository.save(user);

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())
        );
        return ResponseEntity.ok(dto);
    }
}
