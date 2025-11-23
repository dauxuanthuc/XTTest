package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.QuestionSet;
import com.example.demo.payload.QuestionDTO;
import com.example.demo.payload.QuestionSetDTO;
import com.example.demo.service.StudentService;
import com.example.demo.service.QuestionSetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public Practice Controller - không yêu cầu authentication
 * Cho phép người dùng ôn bài mà không cần đăng nhập
 */
@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    private final StudentService studentService;
    private final QuestionSetService questionSetService;

    public PracticeController(StudentService studentService, QuestionSetService questionSetService) {
        this.studentService = studentService;
        this.questionSetService = questionSetService;
    }

    /**
     * Lấy danh sách bộ đề PUBLIC để ôn bài (không cần đăng nhập)
     */
    @GetMapping("/question-sets")
    public ResponseEntity<?> getPublicQuestionSets() {
        try {
            List<QuestionSet> sets = studentService.getPublicQuestionSets();
            List<QuestionSetDTO> dtos = sets.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Lấy các câu hỏi trong một bộ đề PUBLIC (không cần đăng nhập)
     */
    @GetMapping("/question-sets/{setId}/questions")
    public ResponseEntity<?> getQuestionsForPractice(@PathVariable Long setId) {
        try {
            List<Question> questions = questionSetService.getQuestionsByQuestionSetId(setId);
            List<QuestionDTO> dtos = questions.stream()
                    .map(this::convertQuestionToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    private QuestionSetDTO convertToDTO(QuestionSet questionSet) {
        return new QuestionSetDTO(
                questionSet.getId(),
                questionSet.getTitle(),
                questionSet.getDescription(),
                questionSet.getQuestionCount(),
                questionSet.getFileType(),
                questionSet.getCreatedBy().getUsername(),
                questionSet.getCreatedAt(),
                questionSet.getUpdatedAt(),
                questionSet.getVisibility(),
                questionSet.getIsExamScoped()
        );
    }

    private QuestionDTO convertQuestionToDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(), null, null, null
        );
    }
}
