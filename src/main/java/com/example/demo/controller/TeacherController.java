package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.payload.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final UserRepository userRepository;

    public TeacherController(TeacherService teacherService, UserRepository userRepository) {
        this.teacherService = teacherService;
        this.userRepository = userRepository;
    }

    /**
     * Get all classes of current teacher
     */
    @GetMapping("/classes")
    public ResponseEntity<?> getTeacherClasses(Authentication authentication) {
        try {
            String username = authentication.getName();
            User teacher = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<com.example.demo.model.Class> classes = teacherService.getTeacherClasses(teacher.getId());
            return ResponseEntity.ok(new MessageResponse("Classes retrieved", classes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get students in a specific class
     */
    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long classId, Authentication authentication) {
        try {
            String username = authentication.getName();
            User teacher = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<User> students = teacherService.getStudentsByClass(classId);
            
            List<Map<String, Object>> studentData = students.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("username", s.getUsername());
                        map.put("email", s.getEmail());
                        map.put("fullName", s.getFullName());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new MessageResponse("Students retrieved", studentData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Create online exam
     */
    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User teacher = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            String title = (String) request.get("title");
            String description = (String) request.get("description");
            Long questionSetId = Long.valueOf(request.get("questionSetId").toString());
            LocalDateTime startTime = parseDateTime((String) request.get("startTime"));
            LocalDateTime endTime = parseDateTime((String) request.get("endTime"));
            Integer durationMinutes = Integer.parseInt(request.get("durationMinutes").toString());
            Integer passingScore = Integer.parseInt(request.get("passingScore").toString());
            // optional: number of questions to draw from question set
            Integer numberOfQuestions = null;
            if (request.get("numberOfQuestions") != null) {
                try { numberOfQuestions = Integer.valueOf(request.get("numberOfQuestions").toString()); } catch (Exception ignored) {}
            }
            Boolean isRandom = null;
            if (request.get("isRandom") != null) {
                try { isRandom = Boolean.valueOf(request.get("isRandom").toString()); } catch (Exception ignored) {}
            }

            // incoming JSON may have Integer, Long or String values for IDs; normalize to Long
            Object classIdsObj = request.get("classIds");
            List<Long> classIds = new ArrayList<>();
            if (classIdsObj instanceof List<?>) {
                for (Object idObj : (List<?>) classIdsObj) {
                    if (idObj == null) continue;
                    if (idObj instanceof Number) {
                        classIds.add(((Number) idObj).longValue());
                    } else {
                        try {
                            classIds.add(Long.valueOf(idObj.toString()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

                Exam exam = teacherService.createExam(title, description, questionSetId, teacher.getId(),
                    startTime, endTime, durationMinutes, passingScore, classIds, numberOfQuestions, isRandom);

            ExamDTO examDTO = new ExamDTO(exam);
            return ResponseEntity.ok(new MessageResponse("Exam created successfully", examDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Update exam
     */
    @PutMapping("/exams/{examId}")
    public ResponseEntity<?> updateExam(@PathVariable Long examId, @RequestBody Map<String, Object> request,
                                       Authentication authentication) {
        try {
            String username = authentication.getName();
            User teacher = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            String title = (String) request.get("title");
            String description = (String) request.get("description");
            LocalDateTime startTime = parseDateTime((String) request.get("startTime"));
            LocalDateTime endTime = parseDateTime((String) request.get("endTime"));
            Integer durationMinutes = Integer.parseInt(request.get("durationMinutes").toString());
            Integer passingScore = Integer.parseInt(request.get("passingScore").toString());
            Long questionSetId = null;
            if (request.get("questionSetId") != null) {
                try { questionSetId = Long.valueOf(request.get("questionSetId").toString()); } catch (Exception ignored) {}
            }
            Integer numberOfQuestions = null;
            if (request.get("numberOfQuestions") != null) {
                try { numberOfQuestions = Integer.valueOf(request.get("numberOfQuestions").toString()); } catch (Exception ignored) {}
            }
            Boolean isRandom = null;
            if (request.get("isRandom") != null) {
                try { isRandom = Boolean.valueOf(request.get("isRandom").toString()); } catch (Exception ignored) {}
            }

            // incoming JSON may contain Integer or String ids; normalize to Long
            Object classIdsObj = request.get("classIds");
            List<Long> classIds = new ArrayList<>();
            if (classIdsObj instanceof List<?>) {
                for (Object idObj : (List<?>) classIdsObj) {
                    if (idObj == null) continue;
                    if (idObj instanceof Number) {
                        classIds.add(((Number) idObj).longValue());
                    } else {
                        try {
                            classIds.add(Long.valueOf(idObj.toString()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

                Exam exam = teacherService.updateExam(examId, title, description, startTime, endTime,
                    durationMinutes, passingScore, questionSetId, classIds, numberOfQuestions, isRandom);

            ExamDTO examDTO = new ExamDTO(exam);
            return ResponseEntity.ok(new MessageResponse("Exam updated successfully", examDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Delete exam
     */
    @DeleteMapping("/exams/{examId}")
    public ResponseEntity<?> deleteExam(@PathVariable Long examId, Authentication authentication) {
        try {
            teacherService.deleteExam(examId);
            return ResponseEntity.ok(new MessageResponse("Exam deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get all exams of current teacher
     */
    @GetMapping("/exams")
    public ResponseEntity<?> getTeacherExams(Authentication authentication) {
        try {
            String username = authentication.getName();
            User teacher = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<Exam> exams = teacherService.getTeacherExams(teacher.getId());
            List<ExamDTO> examDTOs = exams.stream()
                    .map(ExamDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new MessageResponse("Exams retrieved", examDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get exam details
     */
    @GetMapping("/exams/{examId}")
    public ResponseEntity<?> getExamById(@PathVariable Long examId) {
        try {
            Exam exam = teacherService.getExamById(examId);
            ExamDTO examDTO = new ExamDTO(exam);
            return ResponseEntity.ok(new MessageResponse("Exam retrieved", examDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Publish exam
     */
    @PostMapping("/exams/{examId}/publish")
    public ResponseEntity<?> publishExam(@PathVariable Long examId, Authentication authentication) {
        try {
            Exam exam = teacherService.publishExam(examId);
            ExamDTO examDTO = new ExamDTO(exam);
            return ResponseEntity.ok(new MessageResponse("Exam published successfully", examDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get exam results
     */
    @GetMapping("/exams/{examId}/results")
    public ResponseEntity<?> getExamResults(@PathVariable Long examId, Authentication authentication) {
        try {
            List<ExamResult> results = teacherService.getExamResults(examId);
            List<ExamResultDTO> resultDTOs = results.stream()
                    .map(ExamResultDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new MessageResponse("Results retrieved", resultDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get exam statistics
     */
    @GetMapping("/exams/{examId}/statistics")
    public ResponseEntity<?> getExamStatistics(@PathVariable Long examId, Authentication authentication) {
        try {
            Map<String, Object> statistics = teacherService.getExamStatistics(examId);
            return ResponseEntity.ok(new MessageResponse("Statistics retrieved", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get student exam result
     */
    @GetMapping("/students/{studentId}/exams/{examId}/result")
    public ResponseEntity<?> getStudentExamResult(@PathVariable Long studentId, @PathVariable Long examId,
                                                 Authentication authentication) {
        try {
            ExamResult result = teacherService.getStudentResult(studentId, examId);
            if (result == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Result not found"));
            }

            ExamResultDTO resultDTO = new ExamResultDTO(result);
            return ResponseEntity.ok(new MessageResponse("Result retrieved", resultDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get all results for a student
     */
    @GetMapping("/students/{studentId}/results")
    public ResponseEntity<?> getStudentResults(@PathVariable Long studentId, Authentication authentication) {
        try {
            List<ExamResult> results = teacherService.getStudentResults(studentId);
            List<ExamResultDTO> resultDTOs = results.stream()
                    .map(ExamResultDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new MessageResponse("Results retrieved", resultDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Helper method to parse datetime
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            // Try parsing with ISO format first (2024-01-15T10:30:00)
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            // If that fails, try other formats
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e2) {
                return LocalDateTime.now();
            }
        }
    }
}
