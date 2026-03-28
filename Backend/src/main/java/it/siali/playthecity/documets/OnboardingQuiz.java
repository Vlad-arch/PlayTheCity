package it.siali.playthecity.documets;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "onboarding")
public record OnboardingQuiz(
        @Id
        String id,
        List<OnboardingQuestion> questions
) {
    public record OnboardingQuestion(
            String questionId,
            String text,
            List<OnboardingOption> options
    ) {}

    public record OnboardingOption(
            String optionId,
            String label,
            Map<String, Double> archetypeWeights
    ) {}
}