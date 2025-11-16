package com.example.demo.payload;

import com.example.demo.model.ExamResult;
import java.time.LocalDateTime;

public class ExamResultDTO {
    private Long id;
    private String studentUsername;
    private Long examId;
    private String examTitle;
    private Integer score;
    private Integer maxScore;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Boolean isPassed;
    private LocalDateTime submittedAt;
    private Long durationSeconds;

    public ExamResultDTO() {}

    public ExamResultDTO(ExamResult result) {
        this.id = result.getId();
        this.studentUsername = result.getStudent().getUsername();
        this.examId = result.getExam().getId();
        this.examTitle = result.getExam().getTitle();
        this.score = result.getScore();
        this.maxScore = result.getMaxScore();
        this.correctAnswers = result.getCorrectAnswers();
        this.totalQuestions = result.getTotalQuestions();
        this.isPassed = result.getIsPassed();
        this.submittedAt = result.getSubmittedAt();
        this.durationSeconds = result.getDurationSeconds();
    }

    public ExamResultDTO(Long id, String studentUsername, Long examId, String examTitle, Integer score,
                         Integer maxScore, Integer correctAnswers, Integer totalQuestions, Boolean isPassed,
                         LocalDateTime submittedAt, Long durationSeconds) {
        this.id = id;
        this.studentUsername = studentUsername;
        this.examId = examId;
        this.examTitle = examTitle;
        this.score = score;
        this.maxScore = maxScore;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.isPassed = isPassed;
        this.submittedAt = submittedAt;
        this.durationSeconds = durationSeconds;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentUsername() { return studentUsername; }
    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long durationSeconds) { this.durationSeconds = durationSeconds; }
}

