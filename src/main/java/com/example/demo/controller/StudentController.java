package com.example.demo.controller;

import com.example.demo.model.QuestionSet;
import com.example.demo.payload.SubmitExamRequest;
import com.example.demo.payload.ExamResultDTO;
import com.example.demo.payload.QuestionSetDTO;
import com.example.demo.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/join-class")
    public ResponseEntity<?> joinClass(@RequestBody Map<String, String> body) {
        // Simple stub: this project currently handles enrollments via Teacher or admin flows.
        // For now, return 501 Not Implemented to indicate this can be added.
        return ResponseEntity.status(501).body(Map.of("message", "join-class not implemented yet"));
    }

    @GetMapping("/question-sets")
    public ResponseEntity<?> getVisibleQuestionSets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? null : auth.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        List<QuestionSet> sets = studentService.getVisibleQuestionSets(username);
        List<QuestionSetDTO> dtos = sets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/exams/{accessCode}/start")
    public ResponseEntity<?> startExam(@PathVariable String accessCode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? null : auth.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }
        // allow either numeric examId or accessCode
        try {
            Long id = Long.parseLong(accessCode);
            Map<String, Object> resp = studentService.startExamById(id, username);
            return ResponseEntity.ok(resp);
        } catch (NumberFormatException nfe) {
            Map<String, Object> resp = studentService.startExamByAccessCode(accessCode, username);
            return ResponseEntity.ok(resp);
        }
    }

    @PostMapping("/exams/submit")
    public ResponseEntity<?> submitExam(@RequestBody SubmitExamRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? null : auth.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }
        var result = studentService.submitExam(req, username);
        // Return a compact DTO so frontend doesn't receive nested user/exam entities
        var dto = new ExamResultDTO(result);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? null : auth.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }
        var list = studentService.getStudentResults(username);
        // Map entity list to DTOs for a compact, stable API contract
        var dtoList = list.stream().map(r -> new com.example.demo.payload.ExamResultDTO(r)).toList();
        return ResponseEntity.ok(dtoList);
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
}
