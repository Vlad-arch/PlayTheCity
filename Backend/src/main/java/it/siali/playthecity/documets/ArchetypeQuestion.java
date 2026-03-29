package it.siali.playthecity.documets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "pool_archetype_questions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchetypeQuestion {
    @Id private String id;
    private String text;
    private List<ArchetypeOption> options;

    public ArchetypeQuestion() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<ArchetypeOption> getOptions() { return options; }
    public void setOptions(List<ArchetypeOption> options) { this.options = options; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArchetypeOption {
        private String optionId;
        private String label;
        private Map<String, Double> archetypeWeights; // Chiave: slug esatto

        public ArchetypeOption() { }

        public String getOptionId() { return optionId; }
        public void setOptionId(String optionId) { this.optionId = optionId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Map<String, Double> getArchetypeWeights() { return archetypeWeights; }
        public void setArchetypeWeights(Map<String, Double> archetypeWeights) { this.archetypeWeights = archetypeWeights; }
    }
}