package com.example.demo.repository;

import com.example.demo.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByStudentId(Long studentId);
    ExamResult findByStudentIdAndExamId(Long studentId, Long examId);
    
    void deleteByExamId(Long examId);
    
    @Query("SELECT COUNT(er) FROM ExamResult er WHERE er.exam.id = ?1 AND er.isPassed = true")
    long countPassedByExam(Long examId);
    
    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.exam.id = ?1")
    Double getAverageScoreByExam(Long examId);
}
