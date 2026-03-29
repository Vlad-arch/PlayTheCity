package it.siali.playthecity.dto;

public record GameStepContent(
        String locationName,
        String narrativeStory,
        String curiosity,
        GameQuiz quiz
) {}
