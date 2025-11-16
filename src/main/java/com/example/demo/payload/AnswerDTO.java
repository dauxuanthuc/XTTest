package com.example.demo.payload;

public class AnswerDTO {
    private Long questionId;
    private String answer; // A, B, C, D

    public AnswerDTO() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
