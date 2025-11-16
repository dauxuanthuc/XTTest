package com.example.demo.payload;

public class DashboardStatsDTO {
    private Long totalClasses;
    private Long totalTeachers;
    private Long totalStudents;
    private Long totalQuestionSets;
    private Long totalExamAttempts;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(Long totalClasses, Long totalTeachers, Long totalStudents, 
                             Long totalQuestionSets, Long totalExamAttempts) {
        this.totalClasses = totalClasses;
        this.totalTeachers = totalTeachers;
        this.totalStudents = totalStudents;
        this.totalQuestionSets = totalQuestionSets;
        this.totalExamAttempts = totalExamAttempts;
    }

    public Long getTotalClasses() { return totalClasses; }
    public void setTotalClasses(Long totalClasses) { this.totalClasses = totalClasses; }

    public Long getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(Long totalTeachers) { this.totalTeachers = totalTeachers; }

    public Long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }

    public Long getTotalQuestionSets() { return totalQuestionSets; }
    public void setTotalQuestionSets(Long totalQuestionSets) { this.totalQuestionSets = totalQuestionSets; }

    public Long getTotalExamAttempts() { return totalExamAttempts; }
    public void setTotalExamAttempts(Long totalExamAttempts) { this.totalExamAttempts = totalExamAttempts; }
}
