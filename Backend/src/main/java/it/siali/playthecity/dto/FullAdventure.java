package it.siali.playthecity.dto;

import java.util.List;

public record FullAdventure(
        String adventureTitle,
        String overallNarrative,
        List<GameStepContent> steps
) {}
