package it.siali.playthecity.documets;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pool_cultural_questions")
public class CulturalQuestion {
    @Id
    private String id;
    private String text;
    private int questionDifficulty; // 1 = Facile, 2 = Media, 3 = Difficile
    private String correctOptionId; // Es. "opt_2"
    private List<CulturalOption> options;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(int questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public String getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public List<CulturalOption> getOptions() {
        return options;
    }

    public void setOptions(List<CulturalOption> options) {
        this.options = options;
    }

    public static class CulturalOption {
        private String optionId;
        private String label;

        public String getOptionId() {
            return optionId;
        }

        public void setOptionId(String optionId) {
            this.optionId = optionId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}