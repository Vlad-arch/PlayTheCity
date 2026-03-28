
package it.siali.playthecity.documets;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "onboarding_quizzes")
@Data
public class OnboardingQuiz {
    @Id
    private String id;
    private String version;
    private List<OnboardingQuestion> questions;

    @Data
    public static class OnboardingQuestion {
        private String questionId;
        private String text;
        private List<OnboardingOption> options;
    }

    @Data
    public static class OnboardingOption {
        private String optionId;
        private String label;
        private String imageUrl; // Opzionale per rendere il quiz visivo
        // Mappa i pesi per ogni archetipo (es: "urban-ninja" -> 1.0)
        private Map<String, Double> archetypeWeights; 
    }
}