package it.siali.playthecity.documets;

import it.siali.playthecity.dto.GameQuiz;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "game_sessions")
public class GameSession {
    @Id
    private String id;
    private String userId; // O un identificativo sessione
    private String archetype; // Es. "urban-ninja"
    private String difficulty; // Es. "Medio"
    private String city;
    private String latitude;
    private String longitude;
    private String age;

    private String adventureTitle;
    private String overallNarrative;
    // Il "Tabellone" (Gioco dell'Oca)
    private List<GameStep> steps;
    private int currentStepIndex = 0; // Quale casella sta visitando

    public record GameStep(
            String locationName,
            double lat,
            double lon,
            String description,
            String imageUrl,
            String narrative,
            GameQuiz quiz,
            String source // "WIKIPEDIA" o "FOURSQUARE"
    ) {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArchetype() {
        return archetype;
    }

    public void setArchetype(String archetype) {
        this.archetype = archetype;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAdventureTitle() {
        return adventureTitle;
    }

    public void setAdventureTitle(String adventureTitle) {
        this.adventureTitle = adventureTitle;
    }

    public String getOverallNarrative() {
        return overallNarrative;
    }

    public void setOverallNarrative(String overallNarrative) {
        this.overallNarrative = overallNarrative;
    }

    public List<GameStep> getSteps() {
        return steps;
    }

    public void setSteps(List<GameStep> steps) {
        this.steps = steps;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}