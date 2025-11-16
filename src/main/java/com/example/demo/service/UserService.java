package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUserRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<Role> roles = roleNames.stream()
                .map(roleName -> {
                    String name = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase();
                    return roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Role not found: " + name));
                })
                .collect(Collectors.toSet());
        
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
