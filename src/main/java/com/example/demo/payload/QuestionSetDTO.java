package com.example.demo.payload;

import java.time.LocalDateTime;

public class QuestionSetDTO {
    private Long id;
    private String title;
    private String description;
    private String subject;
    private Integer questionCount;
    private String fileType;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Visibility: PUBLIC or CLASS
    private String visibility;
    // Whether this set is scoped to a specific exam and shouldn't be available for practice
    private Boolean isExamScoped;

    public QuestionSetDTO() {}

    public QuestionSetDTO(Long id, String title, String description, String subject, Integer questionCount,
                          String fileType, String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt,
                          String visibility, Boolean isExamScoped) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.questionCount = questionCount;
        this.fileType = fileType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.visibility = visibility;
        this.isExamScoped = isExamScoped;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Boolean getIsExamScoped() { return isExamScoped; }
    public void setIsExamScoped(Boolean isExamScoped) { this.isExamScoped = isExamScoped; }
}

