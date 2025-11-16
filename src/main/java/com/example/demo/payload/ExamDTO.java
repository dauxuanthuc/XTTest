package com.example.demo.payload;

import com.example.demo.model.Exam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private String accessCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer passingScore;
    private Integer numberOfQuestions;
    private Boolean isRandom;
    private List<Long> classIds;
    private Boolean isPublished;
    private Boolean showAnswers;
    private Long questionSetId;
    private String createdBy;
    private LocalDateTime createdAt;

    public ExamDTO() {}

    public ExamDTO(Exam exam) {
        this.id = exam.getId();
        this.title = exam.getTitle();
        this.description = exam.getDescription();
        this.accessCode = exam.getAccessCode();
        this.startTime = exam.getStartTime();
        this.endTime = exam.getEndTime();
        this.durationMinutes = exam.getDurationMinutes();
        this.numberOfQuestions = exam.getNumberOfQuestions();
        this.passingScore = exam.getPassingScore();
        this.classIds = exam.getClasses().stream().map(c -> c.getId()).collect(Collectors.toList());
        this.isPublished = exam.getIsPublished();
        this.showAnswers = exam.getShowAnswers();
        this.isRandom = exam.getIsRandom();
        this.questionSetId = exam.getQuestionSet().getId();
        
        this.createdBy = exam.getCreatedBy().getUsername();
        this.createdAt = exam.getCreatedAt();
    }

    public ExamDTO(Long id, String title, String description, String accessCode, LocalDateTime startTime,
                   LocalDateTime endTime, Integer durationMinutes, Integer numberOfQuestions, Integer passingScore, Boolean isPublished,
                   Boolean showAnswers, Long questionSetId, String createdBy, LocalDateTime createdAt, Boolean isRandom) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.accessCode = accessCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.numberOfQuestions = numberOfQuestions;
        this.passingScore = passingScore;
        this.isPublished = isPublished;
        this.showAnswers = showAnswers;
        this.questionSetId = questionSetId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isRandom = isRandom;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public Boolean getIsRandom() { return isRandom; }
    public void setIsRandom(Boolean isRandom) { this.isRandom = isRandom; }

    public List<Long> getClassIds() { return classIds; }
    public void setClassIds(List<Long> classIds) { this.classIds = classIds; }

    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }

    public Boolean getShowAnswers() { return showAnswers; }
    public void setShowAnswers(Boolean showAnswers) { this.showAnswers = showAnswers; }

    public Long getQuestionSetId() { return questionSetId; }
    public void setQuestionSetId(Long questionSetId) { this.questionSetId = questionSetId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

