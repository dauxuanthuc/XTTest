package com.example.demo.payload;

import java.util.List;

public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;

    public UserProfileDTO() { }

    public UserProfileDTO(Long id, String username, String email, String fullName, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public List<String> getRoles() { return roles; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
