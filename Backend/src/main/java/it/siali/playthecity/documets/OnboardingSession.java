package it.siali.playthecity.documets;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "onboarding_sessions")
public class OnboardingSession {
    @Id
    private String id;
    private List<String> archetypeQuestionIds; // Solo gli ID delle domande estratte
    private List<String> culturalQuestionIds;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getArchetypeQuestionIds() {
        return archetypeQuestionIds;
    }

    public void setArchetypeQuestionIds(List<String> archetypeQuestionIds) {
        this.archetypeQuestionIds = archetypeQuestionIds;
    }

    public List<String> getCulturalQuestionIds() {
        return culturalQuestionIds;
    }

    public void setCulturalQuestionIds(List<String> culturalQuestionIds) {
        this.culturalQuestionIds = culturalQuestionIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
