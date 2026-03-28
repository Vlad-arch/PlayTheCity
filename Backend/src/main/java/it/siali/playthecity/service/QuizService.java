package it.siali.playthecity.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.siali.playthecity.documets.Archetype;
import it.siali.playthecity.repository.OnboardingQuizRepository;
import it.siali.playthecity.repository.UserProfileRepository;
import org.bson.types.ObjectId;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.siali.playthecity.documets.OnboardingQuiz;
import it.siali.playthecity.documets.OnboardingQuiz.OnboardingQuestion;
import tools.jackson.databind.ObjectMapper;

@Service
public class QuizService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private OnboardingQuizRepository onboardingQuizRepository;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot lo fornisce di default
    /**
     * Genera un nuovo OnboardingQuiz personalizzato chiamando l'LLM
     */
    public OnboardingQuiz generateOnboardingQuiz(List<Archetype> archetypes) {
        // 1. Prepariamo il convertitore per il formato di output
        BeanOutputConverter<OnboardingQuiz> converter = new BeanOutputConverter<>(OnboardingQuiz.class);

        // 2. Convertiamo la lista di oggetti archetipo in una stringa JSON leggibile dall'LLM
        String archetypesJson;
        try {
            archetypesJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(archetypes);
        } catch (Exception e) {
            archetypesJson = archetypes.toString(); // Fallback semplice
        }

        String id = (new ObjectId()).toString();

        // 3. Costruiamo il prompt usando gli archetipi passati come input
        String systemPrompt = """
            Agisci come un Esperto di Game Design e Psicologia del Viaggiatore per il progetto "Play The City".
            
            IL TUO OBIETTIVO:
            Genera un set di 5 domande a scelta multipla per l'onboarding di un nuovo utente. 
            Queste domande devono servire a mappare l'utente su questi specifici Archetipi:
            
            %s
            
            REGOLE DI GENERAZIONE:
            1. SCENARI IMMERSIVI: Ogni domanda deve descrivere una situazione ipotetica (es. "Ti trovi davanti a un portone chiuso...").
            2. OPZIONI BILANCIATE: Ogni domanda deve avere 4 o 5 opzioni di risposta. Ogni opzione deve essere riconducibile a uno o più archetipi sopra elencati.
            3. PESI (WEIGHTS): Per ogni opzione, assegna un valore numerico da 0.0 a 1.0 per gli archetipi corrispondenti. Usa ESATTAMENTE gli "slug" forniti nella lista.
            4. TONO: Il linguaggio deve essere coinvolgente, moderno e adatto a un pubblico Gen Z e Millennial.
            
            FORMATO DI OUTPUT (JSON):
            %s
            Assegna questo id=%s al json di ritorno
            """.formatted(archetypesJson, converter.getFormat(), id);

        // 3. Configurazione del Prompt con il modello specifico (es. Llama su Regolo)
        Prompt prompt = new Prompt(
                systemPrompt,
                OpenAiChatOptions.builder()
                        //.model("Llama-3.3-70B-Instruct")
                        .model("gpt-5-mini")
                        .temperature(1.0)
                        .build()
        );

        // 4. Chiamata all'LLM
        ChatResponse response = chatModel.call(prompt);
        String content = response.getResult().getOutput().getText();

        // 5. Conversione magica da stringa JSON a Record Java
        OnboardingQuiz onboardingQuiz = converter.convert(content);
        onboardingQuiz = onboardingQuizRepository.save(onboardingQuiz);
        return onboardingQuiz;
    }

    /**
     * Calcola l'archetipo dominante basandosi sulle risposte fornite
     */
    public String calculateDominantArchetype(Map<String, String> userAnswers, String quizId) {
        Map<String, Double> totalScores = new HashMap<>();

        OnboardingQuiz quiz = onboardingQuizRepository.findById(quizId).orElseThrow();

        for (OnboardingQuestion q : quiz.questions()) {
            String selectedOptionId = userAnswers.get(q.questionId());
            q.options().stream()
                    .filter(opt -> opt.optionId().equals(selectedOptionId))
                    .findFirst()
                    .ifPresent(opt -> {
                        opt.archetypeWeights().forEach((archetype, weight) ->
                                totalScores.merge(archetype, weight, Double::sum));
                    });
        }

        return Collections.max(totalScores.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}