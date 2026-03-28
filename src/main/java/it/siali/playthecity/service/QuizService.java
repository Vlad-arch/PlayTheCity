package it.siali.playthecity.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.siali.playthecity.documets.OnboardingQuiz;
import it.siali.playthecity.documets.OnboardingQuiz.OnboardingQuestion;

@Service
public class QuizService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Calcola l'archetipo dominante basandosi sulle risposte fornite
     * @param userAnswers Mappa di <QuestionId, OptionId>
     */
    public String calculateDominantArchetype(Map<String, String> userAnswers, OnboardingQuiz quiz) {
        Map<String, Double> totalScores = new HashMap<>();

        for (OnboardingQuestion q : quiz.getQuestions()) {
            String selectedOptionId = userAnswers.get(q.getQuestionId());
            q.getOptions().stream()
                .filter(opt -> opt.getOptionId().equals(selectedOptionId))
                .findFirst()
                .ifPresent(opt -> {
                    opt.getArchetypeWeights().forEach((archetype, weight) -> 
                        totalScores.merge(archetype, weight, Double::sum));
                });
        }

        // Ritorna lo slug dell'archetipo con il punteggio più alto
        return Collections.max(totalScores.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}