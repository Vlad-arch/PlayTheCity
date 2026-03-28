@Document(collection = "archetypes_registry")
@Data
public class Archetype {
    @Id
    private String id;
    private String slug; // es: "mystery-hunter"
    private String displayName;
    private String tagline;
    private NarrativeConfig narrativeEngineConfig;
    private GameplayLogic gameplayLogic;

    @Data
    public static class NarrativeConfig {
        private String toneOfVoice;
        private List<String> vocabularyKeywords;
        private String aiPersonaPrompt; // Il prompt da inviare a Gemini/OpenAI
    }

    @Data
    public static class GameplayLogic {
        private List<String> preferredPoiTypes;
        private Map<String, Double> challengeMix; // es: {"riddles": 0.6, ...}
        private Double difficultyMultiplier;
    }
}