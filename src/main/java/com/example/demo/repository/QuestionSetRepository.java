package com.example.demo.repository;

import com.example.demo.model.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {
    List<QuestionSet> findByCreatedById(Long createdById);
}
