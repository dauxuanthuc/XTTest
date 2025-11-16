package com.example.demo.repository;

import com.example.demo.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    long countByStudentId(Long studentId);
}
