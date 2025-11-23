package com.example.demo.payload;

import java.time.LocalDateTime;

public class AdminNotificationDTO {
    private Long id;
    private String recipientUsername;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public AdminNotificationDTO() {}

    public AdminNotificationDTO(Long id, String recipientUsername, String message, LocalDateTime createdAt, Boolean isRead) {
        this.id = id;
        this.recipientUsername = recipientUsername;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipientUsername() { return recipientUsername; }
    public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
