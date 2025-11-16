package com.example.demo.payload;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public JwtResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
}
