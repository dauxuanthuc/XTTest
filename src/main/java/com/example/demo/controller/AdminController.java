package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.payload.UpdateUserRolesRequest;
import com.example.demo.payload.UserDTO;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String helloAdmin() {
        return "Hello Admin - secured";
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(),
                        u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) return ResponseEntity.notFound().build();
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long id, @RequestBody UpdateUserRolesRequest req) {
        User updatedUser = userService.updateUserRoles(id, req.getRoles());
        UserDTO userDTO = new UserDTO(updatedUser.getId(), updatedUser.getUsername(),
                updatedUser.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new com.example.demo.payload.MessageResponse("User deleted successfully"));
    }
}

