package com.example.demo.payload;

import java.util.List;

public class SubmitExamRequest {
    private Long examId;
    private Long durationSeconds;
    private List<AnswerDTO> answers;

    public SubmitExamRequest() {}

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long durationSeconds) { this.durationSeconds = durationSeconds; }

    public List<AnswerDTO> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDTO> answers) { this.answers = answers; }
}
