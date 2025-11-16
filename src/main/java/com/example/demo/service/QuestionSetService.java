package com.example.demo.service;

import com.example.demo.model.Question;
import com.example.demo.model.QuestionSet;
import com.example.demo.model.User;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuestionSetRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.QuestionFileParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionSetService {

    private final QuestionSetRepository questionSetRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionFileParser fileParser;

    public QuestionSetService(QuestionSetRepository questionSetRepository, 
                             QuestionRepository questionRepository,
                             UserRepository userRepository,
                             QuestionFileParser fileParser) {
        this.questionSetRepository = questionSetRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.fileParser = fileParser;
    }

    /**
     * Create a new question set
     */
    public QuestionSet createQuestionSet(String title, String description, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        QuestionSet questionSet = new QuestionSet();
        questionSet.setTitle(title);
        questionSet.setDescription(description);
        questionSet.setCreatedBy(user);
        questionSet.setCreatedAt(LocalDateTime.now());
        questionSet.setUpdatedAt(LocalDateTime.now());
        questionSet.setQuestionCount(0);
        
        return questionSetRepository.save(questionSet);
    }

    /**
     * Update question set details
     */
    public QuestionSet updateQuestionSet(Long id, String title, String description) {
        QuestionSet questionSet = questionSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question set not found"));
        
        questionSet.setTitle(title);
        questionSet.setDescription(description);
        questionSet.setUpdatedAt(LocalDateTime.now());
        
        return questionSetRepository.save(questionSet);
    }

    /**
     * Delete question set and all its questions
     */
    public void deleteQuestionSet(Long id) {
        QuestionSet questionSet = questionSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question set not found"));
        
        questionRepository.deleteByQuestionSetId(id);
        questionSetRepository.deleteById(id);
    }

    /**
     * Get all question sets
     */
    public List<QuestionSet> getAllQuestionSets() {
        return questionSetRepository.findAll();
    }

    /**
     * Get question set by ID
     */
    public QuestionSet getQuestionSetById(Long id) {
        return questionSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question set not found"));
    }

    /**
     * Get question sets created by a specific user
     */
    public List<QuestionSet> getQuestionSetsByUserId(Long userId) {
        return questionSetRepository.findByCreatedById(userId);
    }

    /**
     * Upload questions from file (Excel, JSON, or Word)
     */
    public QuestionSet uploadQuestionsFromFile(Long questionSetId, MultipartFile file) throws IOException {
        QuestionSet questionSet = getQuestionSetById(questionSetId);
        
        // Parse the file
        List<Question> questions = fileParser.parseFile(file, questionSet);
        
        // Save all questions
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        
        // Update question count
        int totalQuestions = questionRepository.findByQuestionSetId(questionSetId).size();
        questionSet.setQuestionCount(totalQuestions);
        questionSet.setUpdatedAt(LocalDateTime.now());
        
        if (file.getOriginalFilename() != null) {
            questionSet.setFileType(getFileType(file.getOriginalFilename()));
        }
        
        return questionSetRepository.save(questionSet);
    }

    /**
     * Add a single question to a question set
     */
    public Question addQuestion(Long questionSetId, String questionText, 
                               String optionA, String optionB, String optionC, 
                               String optionD, String correctAnswer) {
        QuestionSet questionSet = getQuestionSetById(questionSetId);
        
        Question question = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer, questionSet);
        Question savedQuestion = questionRepository.save(question);
        
        // Update question count
        int totalQuestions = questionRepository.findByQuestionSetId(questionSetId).size();
        questionSet.setQuestionCount(totalQuestions);
        questionSet.setUpdatedAt(LocalDateTime.now());
        questionSetRepository.save(questionSet);
        
        return savedQuestion;
    }

    /**
     * Update a question
     */
    public Question updateQuestion(Long questionId, String questionText,
                                   String optionA, String optionB, String optionC,
                                   String optionD, String correctAnswer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        question.setQuestionText(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectAnswer(correctAnswer);
        question.setUpdatedAt(LocalDateTime.now());
        
        return questionRepository.save(question);
    }

    /**
     * Delete a question
     */
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        Long questionSetId = question.getQuestionSet().getId();
        questionRepository.deleteById(questionId);
        
        // Update question count
        int totalQuestions = questionRepository.findByQuestionSetId(questionSetId).size();
        QuestionSet questionSet = getQuestionSetById(questionSetId);
        questionSet.setQuestionCount(totalQuestions);
        questionSet.setUpdatedAt(LocalDateTime.now());
        questionSetRepository.save(questionSet);
    }

    /**
     * Get all questions in a question set
     */
    public List<Question> getQuestionsByQuestionSetId(Long questionSetId) {
        return questionRepository.findByQuestionSetId(questionSetId);
    }

    /**
     * Get a specific question
     */
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public QuestionSet saveQuestionSet(QuestionSet questionSet) {
        if (questionSet == null) {
            throw new RuntimeException("QuestionSet is null");
        }
        // Ensure question count is up to date if we have an id
        if (questionSet.getId() != null && (questionSet.getQuestionCount() == null || questionSet.getQuestionCount() == 0)) {
            int totalQuestions = questionRepository.findByQuestionSetId(questionSet.getId()).size();
            questionSet.setQuestionCount(totalQuestions);
        }
        questionSet.setUpdatedAt(LocalDateTime.now());
        return questionSetRepository.save(questionSet);
    }

    private String getFileType(String fileName) {
        if (fileName.endsWith(".xlsx")) {
            return "xlsx";
        } else if (fileName.endsWith(".json")) {
            return "json";
        } else if (fileName.endsWith(".docx")) {
            return "docx";
        }
        return "unknown";
    }
}
