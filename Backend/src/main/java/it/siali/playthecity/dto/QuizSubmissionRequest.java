package it.siali.playthecity.dto;


import java.util.Map;

public record QuizSubmissionRequest(
        String quizId,
        Map<String, String> answers
) {}
