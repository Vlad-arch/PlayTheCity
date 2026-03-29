package it.siali.playthecity.dto;

import java.util.List;

public record GameQuiz(
        String question,
        List<String> options,
        int correctAnswerIndex,
        String explanation
) {}