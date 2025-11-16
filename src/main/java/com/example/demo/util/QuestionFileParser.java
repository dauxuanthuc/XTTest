package com.example.demo.util;

import com.example.demo.model.Question;
import com.example.demo.model.QuestionSet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class QuestionFileParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Question> parseFile(MultipartFile file, QuestionSet questionSet) throws IOException {
        String fileName = file.getOriginalFilename();
        
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        if (fileName.endsWith(".xlsx")) {
            return parseExcelFile(file.getInputStream(), questionSet);
        } else if (fileName.endsWith(".json")) {
            return parseJsonFile(file.getInputStream(), questionSet);
        } else if (fileName.endsWith(".docx")) {
            return parseWordFile(file.getInputStream(), questionSet);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Only .xlsx, .json, and .docx are supported.");
        }
    }

    private List<Question> parseExcelFile(InputStream inputStream, QuestionSet questionSet) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;
                
                String questionText = getCellValue(row, 0);
                String optionA = getCellValue(row, 1);
                String optionB = getCellValue(row, 2);
                String optionC = getCellValue(row, 3);
                String optionD = getCellValue(row, 4);
                String correctAnswer = getCellValue(row, 5).toUpperCase();
                
                if (questionText != null && !questionText.isEmpty()) {
                    Question question = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer, questionSet);
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }

    private List<Question> parseJsonFile(InputStream inputStream, QuestionSet questionSet) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try {
            List<Map<String, String>> jsonQuestions = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, String>>>() {});
            
            for (Map<String, String> qMap : jsonQuestions) {
                String questionText = qMap.get("question");
                String optionA = qMap.get("optionA");
                String optionB = qMap.get("optionB");
                String optionC = qMap.get("optionC");
                String optionD = qMap.get("optionD");
                String correctAnswer = qMap.get("answer").toUpperCase();
                
                if (questionText != null && !questionText.isEmpty()) {
                    Question question = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer, questionSet);
                    questions.add(question);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse JSON file: " + e.getMessage(), e);
        }
        
        return questions;
    }

    private List<Question> parseWordFile(InputStream inputStream, QuestionSet questionSet) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder currentQuestion = new StringBuilder();
            List<String> options = new ArrayList<>();
            String correctAnswer = null;
            
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText().trim();
                
                if (text.isEmpty()) continue;
                
                // Detect question line (starts with number or contains "Câu hỏi")
                if (text.matches("^\\d+\\..*") || text.contains("Câu hỏi")) {
                    // Save previous question if exists
                    if (currentQuestion.length() > 0 && options.size() == 4 && correctAnswer != null) {
                        Question question = new Question(
                            currentQuestion.toString(),
                            options.get(0),
                            options.get(1),
                            options.get(2),
                            options.get(3),
                            correctAnswer,
                            questionSet
                        );
                        questions.add(question);
                    }
                    
                    // Reset for new question
                    currentQuestion = new StringBuilder(text.replaceAll("^\\d+\\.\\s*", ""));
                    options.clear();
                    correctAnswer = null;
                }
                // Detect options (A., B., C., D.)
                else if (text.matches("^[A-D]\\..+")) {
                    options.add(text.replaceAll("^[A-D]\\.\\s*", ""));
                }
                // Detect answer line
                else if (text.toLowerCase().contains("đáp án") || text.toLowerCase().contains("answer")) {
                    correctAnswer = text.replaceAll(".*[^A-D]([A-D])[^A-D]*", "$1").toUpperCase();
                } else if (currentQuestion.length() > 0 && options.size() < 4) {
                    // Continuation of question text
                    currentQuestion.append(" ").append(text);
                }
            }
            
            // Add last question
            if (currentQuestion.length() > 0 && options.size() == 4 && correctAnswer != null) {
                Question question = new Question(
                    currentQuestion.toString(),
                    options.get(0),
                    options.get(1),
                    options.get(2),
                    options.get(3),
                    correctAnswer,
                    questionSet
                );
                questions.add(question);
            }
        }
        
        return questions;
    }

    private String getCellValue(XSSFRow row, int cellIndex) {
        try {
            var cell = row.getCell(cellIndex);
            if (cell == null) return "";
            
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf((int) cell.getNumericCellValue());
                default:
                    return cell.toString();
            }
        } catch (Exception e) {
            return "";
        }
    }
}
