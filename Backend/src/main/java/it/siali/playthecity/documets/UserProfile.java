package it.siali.playthecity.documets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "user_profiles")
@Data
public class UserProfile {
    @Id
    private String id;
    private AuthData auth;
    private Demographics demographics;
    private DnaProfile dnaProfile;
    private Gamification gamification;
    private ActivityHistory activityHistory;

    @Data
    public static class AuthData {
        private String username;
        private String email;
        private LocalDateTime createdAt;
    }

    @Data
    public static class DnaProfile {
        private String primaryArchetype; // slug dell'archetipo
        private Map<String, Integer> archetypeScores; // es: {"urban-ninja": 45, ...}
        private List<String> interests;
        private String narrativeTone;
    }

    @Data
    public static class Gamification {
        private int level;
        private int totalXp;
        private List<Badge> badges;
    }
}