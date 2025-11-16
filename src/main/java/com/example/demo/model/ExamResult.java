package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_result")
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private Integer score; // Điểm số

    @Column(name = "max_score")
    private Integer maxScore; // Điểm tối đa

    @Column(name = "correct_answers")
    private Integer correctAnswers; // Số câu đúng

    @Column(name = "total_questions")
    private Integer totalQuestions; // Tổng câu hỏi

    @Column(name = "is_passed")
    private Boolean isPassed; // Có đạt?

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // Thời gian nộp

    @Column(name = "duration_seconds")
    private Long durationSeconds; // Thời gian làm (giây)

    @Column(columnDefinition = "LONGTEXT")
    private String answers; // JSON lưu trữ câu trả lời

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ExamResult() {
        this.createdAt = LocalDateTime.now();
        this.submittedAt = LocalDateTime.now();
    }

    public ExamResult(User student, Exam exam, Integer score, Integer maxScore, Integer correctAnswers, Integer totalQuestions) {
        this.student = student;
        this.exam = exam;
        this.score = score;
        this.maxScore = maxScore;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.isPassed = score >= exam.getPassingScore();
        this.createdAt = LocalDateTime.now();
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

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

    public String getAnswers() { return answers; }
    public void setAnswers(String answers) { this.answers = answers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
