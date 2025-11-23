package com.example.demo.payload;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public NotificationDTO() {}

    public NotificationDTO(Long id, String message, LocalDateTime createdAt, Boolean isRead) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
