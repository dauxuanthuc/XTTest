package com.example.demo.repository;

import com.example.demo.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCreatedById(Long teacherId);
    List<Exam> findByClassesId(Long classId);
    Exam findByAccessCode(String accessCode);
    
    @Query("SELECT e FROM Exam e JOIN e.classes c WHERE c.id = ?1 AND e.createdBy.id = ?2")
    List<Exam> findByClassAndTeacher(Long classId, Long teacherId);
}
