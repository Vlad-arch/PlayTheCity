package it.siali.playthecity.documets;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "archetypes")
public class Archetype {
    @Id
    private String id;
    private String slug; // es: "mystery-hunter"
    private String displayName;
    private String tagline;
    private NarrativeConfig narrativeEngineConfig;
    private GameplayLogic gameplayLogic;

    public static class NarrativeConfig {
        private String toneOfVoice;
        private List<String> vocabularyKeywords;
        private String aiPersonaPrompt; // Il prompt da inviare a Gemini/OpenAI

        public String getToneOfVoice() {
            return toneOfVoice;
        }

        public void setToneOfVoice(String toneOfVoice) {
            this.toneOfVoice = toneOfVoice;
        }

        public List<String> getVocabularyKeywords() {
            return vocabularyKeywords;
        }

        public void setVocabularyKeywords(List<String> vocabularyKeywords) {
            this.vocabularyKeywords = vocabularyKeywords;
        }

        public String getAiPersonaPrompt() {
            return aiPersonaPrompt;
        }

        public void setAiPersonaPrompt(String aiPersonaPrompt) {
            this.aiPersonaPrompt = aiPersonaPrompt;
        }
    }

    public static class GameplayLogic {
        private List<String> preferredPoiTypes;
        private Map<String, Double> challengeMix; // es: {"riddles": 0.6, ...}
        private Double difficultyMultiplier;

        public List<String> getPreferredPoiTypes() {
            return preferredPoiTypes;
        }

        public void setPreferredPoiTypes(List<String> preferredPoiTypes) {
            this.preferredPoiTypes = preferredPoiTypes;
        }

        public Map<String, Double> getChallengeMix() {
            return challengeMix;
        }

        public void setChallengeMix(Map<String, Double> challengeMix) {
            this.challengeMix = challengeMix;
        }

        public Double getDifficultyMultiplier() {
            return difficultyMultiplier;
        }

        public void setDifficultyMultiplier(Double difficultyMultiplier) {
            this.difficultyMultiplier = difficultyMultiplier;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public NarrativeConfig getNarrativeEngineConfig() {
        return narrativeEngineConfig;
    }

    public void setNarrativeEngineConfig(NarrativeConfig narrativeEngineConfig) {
        this.narrativeEngineConfig = narrativeEngineConfig;
    }

    public GameplayLogic getGameplayLogic() {
        return gameplayLogic;
    }

    public void setGameplayLogic(GameplayLogic gameplayLogic) {
        this.gameplayLogic = gameplayLogic;
    }
}