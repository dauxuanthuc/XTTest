package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tên đề thi

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Giáo viên tạo

    @ManyToOne
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet; // Bộ câu hỏi được dùng

    @Column(name = "access_code")
    private String accessCode; // Mã tham gia (random 6 ký tự)

    @Column(name = "start_time")
    private LocalDateTime startTime; // Thời gian bắt đầu

    @Column(name = "end_time")
    private LocalDateTime endTime; // Thời gian kết thúc

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // Thời gian làm bài (phút)

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions; // Số câu lấy từ bộ đề (null = dùng toàn bộ)

    @Column(name = "is_random")
    private Boolean isRandom = false; // Lấy câu ngẫu nhiên từ bộ đề?

    @Column(name = "passing_score")
    private Integer passingScore; // Điểm đạt (%)

    @ManyToMany
    @JoinTable(
        name = "exam_class",
        joinColumns = @JoinColumn(name = "exam_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<Class> classes = new HashSet<>(); // Các lớp được gán

    @Column(name = "is_published")
    private Boolean isPublished = false; // Đã công bố?

    @Column(name = "show_answers")
    private Boolean showAnswers = false; // Hiển thị đáp án sau khi nộp?

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Exam() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public QuestionSet getQuestionSet() { return questionSet; }
    public void setQuestionSet(QuestionSet questionSet) { this.questionSet = questionSet; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public Boolean getIsRandom() { return isRandom; }
    public void setIsRandom(Boolean isRandom) { this.isRandom = isRandom; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public Set<Class> getClasses() { return classes; }
    public void setClasses(Set<Class> classes) { this.classes = classes; }

    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }

    public Boolean getShowAnswers() { return showAnswers; }
    public void setShowAnswers(Boolean showAnswers) { this.showAnswers = showAnswers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
