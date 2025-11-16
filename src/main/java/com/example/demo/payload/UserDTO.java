package com.example.demo.payload;

import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private List<String> roles;

    public UserDTO(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
}
