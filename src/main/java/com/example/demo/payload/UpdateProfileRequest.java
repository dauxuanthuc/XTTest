package com.example.demo.payload;

public class UpdateProfileRequest {
    private String email;
    private String fullName;
    private String password; // optional: when present, update password

    public UpdateProfileRequest() { }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
