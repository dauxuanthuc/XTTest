package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.Class;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final QuestionSetRepository questionSetRepository;
    private final EnrollmentRepository enrollmentRepository;

    public TeacherService(ExamRepository examRepository, ExamResultRepository examResultRepository,
                         UserRepository userRepository, ClassRepository classRepository,
                         QuestionSetRepository questionSetRepository, EnrollmentRepository enrollmentRepository) {
        this.examRepository = examRepository;
        this.examResultRepository = examResultRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.questionSetRepository = questionSetRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Get all classes of a teacher
     */
    public List<Class> getTeacherClasses(Long teacherId) {
        List<Class> allClasses = classRepository.findAll();
        return allClasses.stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                .collect(Collectors.toList());
    }

    /**
     * Get students in a specific class
     */
    public List<User> getStudentsByClass(Long classId) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassEntityId(classId);
        return enrollments.stream().map(Enrollment::getStudent).collect(Collectors.toList());
    }

    /**
     * Create online exam
     */
    public Exam createExam(String title, String description, Long questionSetId, Long teacherId,
                          LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes,
                          Integer passingScore, List<Long> classIds,
                          Integer numberOfQuestions, Boolean isRandom) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        QuestionSet questionSet = questionSetRepository.findById(questionSetId)
                .orElseThrow(() -> new RuntimeException("Question set not found"));

        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setQuestionSet(questionSet);
        exam.setCreatedBy(teacher);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setDurationMinutes(durationMinutes);
        exam.setNumberOfQuestions(numberOfQuestions);
        exam.setIsRandom(isRandom == null ? false : isRandom);
        exam.setPassingScore(passingScore);
        exam.setAccessCode(generateAccessCode());
        exam.setIsPublished(false);

        // Add classes
        if (classIds != null) {
            Set<Class> classes = new HashSet<>();
            for (Long classId : classIds) {
                Class classEntity = classRepository.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found"));
                classes.add(classEntity);
            }
            exam.setClasses(classes);
        }

        return examRepository.save(exam);
    }

    /**
     * Update exam
     */
    public Exam updateExam(Long examId, String title, String description, LocalDateTime startTime,
                          LocalDateTime endTime, Integer durationMinutes, Integer passingScore, Long questionSetId,
                          List<Long> classIds, Integer numberOfQuestions, Boolean isRandom) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setTitle(title);
        exam.setDescription(description);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setDurationMinutes(durationMinutes);
        exam.setNumberOfQuestions(numberOfQuestions);
        exam.setIsRandom(isRandom == null ? false : isRandom);
        exam.setPassingScore(passingScore);
        exam.setUpdatedAt(LocalDateTime.now());

        // Update question set if provided
        if (questionSetId != null) {
            com.example.demo.model.QuestionSet questionSet = questionSetRepository.findById(questionSetId)
                    .orElseThrow(() -> new RuntimeException("Question set not found"));
            exam.setQuestionSet(questionSet);
        }

        if (classIds != null) {
            Set<Class> classes = new HashSet<>();
            for (Long classId : classIds) {
                Class classEntity = classRepository.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found"));
                classes.add(classEntity);
            }
            exam.setClasses(classes);
        }

        return examRepository.save(exam);
    }

    /**
     * Publish exam
     */
    public Exam publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        exam.setIsPublished(true);
        exam.setUpdatedAt(LocalDateTime.now());
        return examRepository.save(exam);
    }

    /**
     * Delete exam
     */
    public void deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        // Delete all results
        examResultRepository.deleteByExamId(examId);
        
        // Delete exam
        examRepository.deleteById(examId);
    }

    /**
     * Get all exams for a teacher
     */
    public List<Exam> getTeacherExams(Long teacherId) {
        return examRepository.findByCreatedById(teacherId);
    }

    /**
     * Get exam details
     */
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }

    /**
     * Get exam results
     */
    public List<ExamResult> getExamResults(Long examId) {
        return examResultRepository.findByExamId(examId);
    }

    /**
     * Get exam statistics
     */
    public Map<String, Object> getExamStatistics(Long examId) {
        List<ExamResult> results = examResultRepository.findByExamId(examId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", results.size());
        stats.put("passedCount", results.stream().filter(r -> r.getIsPassed()).count());
        stats.put("failedCount", results.stream().filter(r -> !r.getIsPassed()).count());
        
        if (!results.isEmpty()) {
            double avgScore = results.stream()
                    .mapToInt(ExamResult::getScore)
                    .average()
                    .orElse(0);
            stats.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
            
            int highestScore = results.stream()
                    .mapToInt(ExamResult::getScore)
                    .max()
                    .orElse(0);
            stats.put("highestScore", highestScore);
            
            int lowestScore = results.stream()
                    .mapToInt(ExamResult::getScore)
                    .min()
                    .orElse(0);
            stats.put("lowestScore", lowestScore);
        }
        
        return stats;
    }

    /**
     * Get student exam result
     */
    public ExamResult getStudentResult(Long studentId, Long examId) {
        return examResultRepository.findByStudentIdAndExamId(studentId, examId);
    }

    /**
     * Get all results for a student
     */
    public List<ExamResult> getStudentResults(Long studentId) {
        return examResultRepository.findByStudentId(studentId);
    }

    /**
     * Generate random access code (6 characters)
     */
    private String generateAccessCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
