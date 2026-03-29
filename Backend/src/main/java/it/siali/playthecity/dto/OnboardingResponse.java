package it.siali.playthecity.dto;

import it.siali.playthecity.documets.ArchetypeQuestion;
import it.siali.playthecity.documets.CulturalQuestion;
import java.util.List;

public record OnboardingResponse(
        String sessionId,
        List<ArchetypeQuestion> behavioralQuestions,
        List<CulturalQuestion> culturalQuestions
) {}