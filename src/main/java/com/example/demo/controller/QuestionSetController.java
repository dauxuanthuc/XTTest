package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.QuestionSet;
import com.example.demo.model.User;
import com.example.demo.payload.MessageResponse;
import com.example.demo.payload.QuestionDTO;
import com.example.demo.payload.QuestionSetDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.QuestionSetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/question-sets")
public class QuestionSetController {

    private final QuestionSetService questionSetService;
    private final UserRepository userRepository;

    public QuestionSetController(QuestionSetService questionSetService, UserRepository userRepository) {
        this.questionSetService = questionSetService;
        this.userRepository = userRepository;
    }

    /**
     * Get all question sets (public access)
     */
    @GetMapping
    public ResponseEntity<?> getAllQuestionSets() {
        try {
            List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
            List<QuestionSetDTO> dtos = questionSets.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error retrieving question sets: " + e.getMessage()));
        }
    }

    /**
     * Get question set by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionSetById(@PathVariable Long id) {
        try {
            QuestionSet questionSet = questionSetService.getQuestionSetById(id);
            return ResponseEntity.ok(convertToDTO(questionSet));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Create a new question set (Admin only)
     */
    @PostMapping
    public ResponseEntity<?> createQuestionSet(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String title = request.get("title");
            String description = request.get("description");
            
            if (title == null || title.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Title is required"));
            }
            
            // Extract username from authentication and get user by username
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Allow optional visibility and examScoped flags
            String visibility = request.get("visibility");
            String subject = request.get("subject");
            Boolean isExamScoped = false;
            if (request.get("isExamScoped") != null) {
                isExamScoped = Boolean.valueOf(request.get("isExamScoped"));
            }
            if (visibility == null || visibility.isEmpty()) visibility = "CLASS"; // default: class-only

            QuestionSet questionSet = questionSetService.createQuestionSet(title, description, currentUser.getId());
            if (subject != null) questionSet.setSubject(subject);
            questionSet.setVisibility(visibility);
            questionSet.setIsExamScoped(isExamScoped);
            questionSet = questionSetService.saveQuestionSet(questionSet);
            return ResponseEntity.ok(convertToDTO(questionSet));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error creating question set: " + e.getMessage()));
        }
    }

    /**
     * Update question set (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestionSet(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String description = request.get("description");
            
            QuestionSet questionSet = questionSetService.updateQuestionSet(id, title, description);
            
            // Update visibility and isExamScoped if provided
            String visibility = request.get("visibility");
            String isExamScopedStr = request.get("isExamScoped");
            String subject = request.get("subject");
            
            if (visibility != null && !visibility.isEmpty()) {
                questionSet.setVisibility(visibility);
            }
            if (subject != null) questionSet.setSubject(subject);
            if (isExamScopedStr != null) {
                questionSet.setIsExamScoped(Boolean.parseBoolean(isExamScopedStr));
            }
            
            questionSet = questionSetService.saveQuestionSet(questionSet);
            return ResponseEntity.ok(convertToDTO(questionSet));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Delete question set (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestionSet(@PathVariable Long id) {
        try {
            questionSetService.deleteQuestionSet(id);
            return ResponseEntity.ok(new MessageResponse("Question set deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Upload questions from file (Excel, JSON, Word)
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadQuestions(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("File cannot be empty"));
            }
            
            QuestionSet questionSet = questionSetService.uploadQuestionsFromFile(id, file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Questions uploaded successfully");
            response.put("questionCount", questionSet.getQuestionCount());
            response.put("fileType", questionSet.getFileType());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error reading file: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get all questions in a question set
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {
        try {
            List<Question> questions = questionSetService.getQuestionsByQuestionSetId(id);
            List<QuestionDTO> dtos = questions.stream()
                    .map(this::convertQuestionToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Add a single question to a question set
     */
    @PostMapping("/{id}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String questionText = request.get("questionText");
            String optionA = request.get("optionA");
            String optionB = request.get("optionB");
            String optionC = request.get("optionC");
            String optionD = request.get("optionD");
            String correctAnswer = request.get("correctAnswer");
            
            if (questionText == null || questionText.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Question text is required"));
            }
            
            Question question = questionSetService.addQuestion(id, questionText, optionA, optionB, optionC, optionD, correctAnswer);
            return ResponseEntity.ok(new MessageResponse("Question added successfully with ID: " + question.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Update a question
     */
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody Map<String, String> request) {
        try {
            String questionText = request.get("questionText");
            String optionA = request.get("optionA");
            String optionB = request.get("optionB");
            String optionC = request.get("optionC");
            String optionD = request.get("optionD");
            String correctAnswer = request.get("correctAnswer");
            
            Question question = questionSetService.updateQuestion(questionId, questionText, optionA, optionB, optionC, optionD, correctAnswer);
            return ResponseEntity.ok(convertQuestionToDTO(question));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Delete a question
     */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        try {
            questionSetService.deleteQuestion(questionId);
            return ResponseEntity.ok(new MessageResponse("Question deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get a specific question
     */
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable Long questionId) {
        try {
            Question question = questionSetService.getQuestionById(questionId);
            return ResponseEntity.ok(convertQuestionToDTO(question));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    private QuestionSetDTO convertToDTO(QuestionSet questionSet) {
        return new QuestionSetDTO(
                questionSet.getId(),
                questionSet.getTitle(),
                questionSet.getDescription(),
                questionSet.getSubject(),
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
                question.getCorrectAnswer(),
                question.getQuestionSet().getId(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
