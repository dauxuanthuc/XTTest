package com.example.demo.service;

import com.example.demo.payload.DashboardStatsDTO;
import com.example.demo.repository.ClassRepository;
import com.example.demo.repository.ExamAttemptRepository;
import com.example.demo.repository.QuestionSetRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final QuestionSetRepository questionSetRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public DashboardService(ClassRepository classRepository,
                           UserRepository userRepository,
                           QuestionSetRepository questionSetRepository,
                           ExamAttemptRepository examAttemptRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.questionSetRepository = questionSetRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    /**
     * Get system-wide statistics for the dashboard
     */
    public DashboardStatsDTO getDashboardStats() {
        long totalClasses = classRepository.count();
        long totalTeachers = userRepository.countTeachers();
        long totalStudents = userRepository.countStudents();
        long totalQuestionSets = questionSetRepository.count();
        long totalExamAttempts = examAttemptRepository.count();

        return new DashboardStatsDTO(
                totalClasses,
                totalTeachers,
                totalStudents,
                totalQuestionSets,
                totalExamAttempts
        );
    }
}
