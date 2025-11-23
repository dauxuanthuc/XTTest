package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.payload.AnswerDTO;
import com.example.demo.payload.SubmitExamRequest;
import com.example.demo.payload.QuestionDTO;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    // Trả về các bộ đề PUBLIC, không phải exam-scoped
    private final QuestionSetRepository questionSetRepository;

    public StudentService(ExamRepository examRepository, QuestionRepository questionRepository,
                          UserRepository userRepository, EnrollmentRepository enrollmentRepository,
                          ExamResultRepository examResultRepository,
                          QuestionSetRepository questionSetRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.examResultRepository = examResultRepository;
        this.questionSetRepository = questionSetRepository;
    }

    public List<QuestionSet> getPublicQuestionSets() {
        List<QuestionSet> allSets = questionSetRepository.findAll();
        List<QuestionSet> publicSets = new ArrayList<>();
        for (QuestionSet set : allSets) {
            if ("PUBLIC".equals(set.getVisibility()) && !Boolean.TRUE.equals(set.getIsExamScoped())) {
                publicSets.add(set);
            }
        }
        return publicSets;
    }

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ExamResultRepository examResultRepository;

    // Removed duplicate constructor to ensure all final fields are initialized

    public Map<String, Object> startExamByAccessCode(String accessCode, String username) {
        Exam exam = examRepository.findByAccessCode(accessCode);
        if (exam == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found");
        }

        if (exam.getIsPublished() == null || !exam.getIsPublished()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Exam is not published");
        }

        // Optional: check enrollment: allow if exam is assigned to classes and student is enrolled
        User student = userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // If the exam has assigned classes, require enrollment — except when the exam
        // was created by an admin account (admin-created exams are visible to whole system).
        if (exam.getClasses() != null && !exam.getClasses().isEmpty()) {
            boolean createdByAdmin = false;
            if (exam.getCreatedBy() != null && exam.getCreatedBy().getRoles() != null) {
                createdByAdmin = exam.getCreatedBy().getRoles().stream()
                        .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            }

            if (!createdByAdmin) {
                boolean enrolled = false;
                for (com.example.demo.model.Class c : exam.getClasses()) {
                    List<Enrollment> enrollments = enrollmentRepository.findByClassEntityId(c.getId());
                    for (Enrollment e : enrollments) {
                        if (e.getStudent().getId().equals(student.getId())) {
                            enrolled = true;
                            break;
                        }
                    }
                    if (enrolled) break;
                }
                if (!enrolled) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not enrolled in exam classes");
                }
            }
        }

        // Load questions from question set and optionally take a subset
        List<Question> questions = questionRepository.findByQuestionSetId(exam.getQuestionSet().getId());
        Integer number = exam.getNumberOfQuestions();
        Boolean randomize = exam.getIsRandom();
        List<Question> selected = questions;
        if (number != null && number > 0 && number < questions.size()) {
            if (randomize != null && randomize) {
                Collections.shuffle(selected = new ArrayList<>(questions));
            } else {
                selected = new ArrayList<>(questions).subList(0, number);
            }
            if (selected.size() > number) selected = selected.subList(0, number);
        }

        List<QuestionDTO> dtos = selected.stream().map(q -> new QuestionDTO(
                q.getId(), q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                null, q.getQuestionSet().getId(), q.getCreatedAt(), q.getUpdatedAt()
        )).collect(Collectors.toList());

        long durationSeconds = 0;
        if (exam.getDurationMinutes() != null) durationSeconds = exam.getDurationMinutes() * 60L;
        else if (exam.getStartTime() != null && exam.getEndTime() != null) {
            Duration d = Duration.between(exam.getStartTime(), exam.getEndTime());
            durationSeconds = d.getSeconds();
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("examId", exam.getId());
        resp.put("title", exam.getTitle());
        resp.put("questions", dtos);
        resp.put("durationSeconds", durationSeconds);
        return resp;
    }

    public Map<String, Object> startExamById(Long examId, String username) {
        Exam exam = examRepository.findById(examId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));

        if (exam.getIsPublished() == null || !exam.getIsPublished()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Exam is not published");
        }

        User student = userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // If the exam has assigned classes, require enrollment — except when the exam
        // was created by an admin account (admin-created exams are visible to whole system).
        if (exam.getClasses() != null && !exam.getClasses().isEmpty()) {
            boolean createdByAdmin = false;
            if (exam.getCreatedBy() != null && exam.getCreatedBy().getRoles() != null) {
                createdByAdmin = exam.getCreatedBy().getRoles().stream()
                        .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            }

            if (!createdByAdmin) {
                boolean enrolled = false;
                for (com.example.demo.model.Class c : exam.getClasses()) {
                    List<Enrollment> enrollments = enrollmentRepository.findByClassEntityId(c.getId());
                    for (Enrollment e : enrollments) {
                        if (e.getStudent().getId().equals(student.getId())) {
                            enrolled = true;
                            break;
                        }
                    }
                    if (enrolled) break;
                }
                if (!enrolled) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not enrolled in exam classes");
                }
            }
        }

        List<Question> questions = questionRepository.findByQuestionSetId(exam.getQuestionSet().getId());
        Integer number = exam.getNumberOfQuestions();
        Boolean randomize = exam.getIsRandom();
        List<Question> selected = questions;
        if (number != null && number > 0 && number < questions.size()) {
            if (randomize != null && randomize) {
                Collections.shuffle(selected = new ArrayList<>(questions));
            } else {
                selected = new ArrayList<>(questions).subList(0, number);
            }
            if (selected.size() > number) selected = selected.subList(0, number);
        }

        List<QuestionDTO> dtos = selected.stream().map(q -> new QuestionDTO(
                q.getId(), q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                null, q.getQuestionSet().getId(), q.getCreatedAt(), q.getUpdatedAt()
        )).collect(Collectors.toList());

        long durationSeconds = 0;
        if (exam.getDurationMinutes() != null) durationSeconds = exam.getDurationMinutes() * 60L;
        else if (exam.getStartTime() != null && exam.getEndTime() != null) {
            Duration d = Duration.between(exam.getStartTime(), exam.getEndTime());
            durationSeconds = d.getSeconds();
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("examId", exam.getId());
        resp.put("title", exam.getTitle());
        resp.put("questions", dtos);
        resp.put("durationSeconds", durationSeconds);
        return resp;
    }

    public ExamResult submitExam(SubmitExamRequest req, String username) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (req.getExamId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "examId is required");
        }
        Exam exam = examRepository.findById(req.getExamId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));

        User student = userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (exam.getQuestionSet() == null || exam.getQuestionSet().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam has no question set");
        }
        List<Question> questions = questionRepository.findByQuestionSetId(exam.getQuestionSet().getId());
        Map<Long, Question> qmap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        int total = questions.size();
        int correct = 0;
        if (req.getAnswers() != null) {
            for (AnswerDTO a : req.getAnswers()) {
                Question q = qmap.get(a.getQuestionId());
                if (q != null && q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase(a.getAnswer())) {
                    correct++;
                }
            }
        }

        int score = 0;
        int maxScore = 100;
        if (total > 0) {
            score = (int) Math.round((double) correct / total * maxScore);
        }

        ExamResult result = new ExamResult();
        result.setExam(exam);
        result.setStudent(student);
        result.setCorrectAnswers(correct);
        result.setTotalQuestions(total);
        result.setScore(score);
        result.setMaxScore(maxScore);
        result.setIsPassed(exam.getPassingScore() == null ? score >= 50 : score >= exam.getPassingScore());
        result.setSubmittedAt(LocalDateTime.now());
        result.setDurationSeconds(req.getDurationSeconds());

        try {
            ObjectMapper om = new ObjectMapper();
            result.setAnswers(om.writeValueAsString(req.getAnswers()));
        } catch (Exception ex) {
            result.setAnswers("[]");
        }

        try {
            return examResultRepository.save(result);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save exam result");
        }
    }

    public List<ExamResult> getStudentResults(String username) {
        User student = userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return examResultRepository.findByStudentId(student.getId());
    }

    /**
     * Get question sets visible to a student for practice (excluding exam-scoped sets).
     * - PUBLIC sets: always visible
     * - CLASS sets: visible (teachers can restrict enrollment at class level separately)
     * - Exam-scoped sets: never shown (isExamScoped=true)
     */
    public List<QuestionSet> getVisibleQuestionSets(String username) {
        // Verify user exists
        userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Collect all question sets referenced by exams
        Map<Long, QuestionSet> setMap = new HashMap<>();
        
        List<Exam> allExams = examRepository.findAll();
        for (Exam exam : allExams) {
            if (exam.getQuestionSet() != null) {
                setMap.put(exam.getQuestionSet().getId(), exam.getQuestionSet());
            }
        }

        // Filter out exam-scoped sets
        List<QuestionSet> visibleSets = new ArrayList<>();
        for (QuestionSet qs : setMap.values()) {
            // Skip exam-scoped sets (these are only for specific exams, not for general practice)
            if (qs.getIsExamScoped() != null && qs.getIsExamScoped()) {
                continue;
            }

            // Include PUBLIC and CLASS sets (null visibility defaults to CLASS)
            String visibility = qs.getVisibility();
            if ("PUBLIC".equals(visibility) || "CLASS".equals(visibility) || visibility == null) {
                visibleSets.add(qs);
            }
        }

        return visibleSets;
    }
}
