package it.siali.playthecity.service;

import it.siali.playthecity.documets.*;
import it.siali.playthecity.dto.FinalizeRequest;
import it.siali.playthecity.dto.OnboardingResponse;
import it.siali.playthecity.dto.ProfileResult;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonalizationEngineService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    // =========================================================================
    // 1. GENERAZIONE IN BACKGROUND (Da chiamare via API Admin o Cronjob)
    // =========================================================================

    public void generateArchetypeQuestionsBackground(List<Archetype> archetypes, int amount) {
        BeanOutputConverter<List<ArchetypeQuestion>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<ArchetypeQuestion>>() {}
        );

        // Estraiamo gli slug per blindare l'LLM
        List<String> allowedSlugs = archetypes.stream().map(Archetype::getSlug).toList();
        String slugsString = String.join(", ", allowedSlugs);

        String archetypesJson = "[]";
        try {
            archetypesJson = objectMapper.writeValueAsString(archetypes);
        } catch (Exception ignored) {}

        /*String systemPrompt = """
            Genera %d domande situazionali per capire lo stile di gioco di un utente in un'app di esplorazione urbana.
            
            REGOLE TASSATIVE:
            1. STRUTTURA: Devi restituire ESATTAMENTE un ARRAY JSON (inizia con '[' e finisce con ']'). Anche se generi una sola domanda, DEVE essere dentro un array.
            2. ID Domanda: Lascia il campo "id" nullo (null).
            3. ID Opzioni: Usa il formato rigoroso "opt_1", "opt_2", "opt_3", "opt_4".
            4. Pesi (archetypeWeights): Usa ESATTAMENTE ed UNICAMENTE queste chiavi: [%s]. NON INVENTARE ALTRI NOMI.
            5. Valori dei Pesi: I valori DEVONO essere DECIMALI compresi tra 0.0 e 1.0 (es. 0.5, 0.8, 1.0). VIETATO usare numeri interi come 5 o 3.
            
            Ecco la definizione completa degli archetipi su cui basarti:
            %s
            
            FORMATO DI OUTPUT:
            %s
            """.formatted(amount, slugsString, archetypesJson, converter.getFormat());*/

        String systemPrompt = """
            Agisci come un Esperto di Game Designer e Psicologia del Viaggiatore per il progetto "Play The City".
            
            IL TUO OBIETTIVO:
            Genera %d scenari immersivi di onboarding in lingua: italiana. Ogni domanda deve trasportare l'utente in una situazione ipotetica 
            in città (es. "Ti trovi davanti a un portone antico chiuso...", "Vedi un vicolo laterale con murales...", ecc.).
            
            REGOLE DI GENERAZIONE:
            1. SCENARI E TONO: Il linguaggio deve essere coinvolgente, moderno (Gen Z/Millennial) e ricco di atmosfera.
            2. ARCHETIPI: Usa ESATTAMENTE questi slug per mappare le risposte: [%s].
            3. OPZIONI: Ogni scenario deve avere 4 opzioni. Ogni opzione deve riflettere la personalità di uno o più archetipi.
            4. PESI (WEIGHTS): I valori DEVONO essere DECIMALI compresi tra 0.1 e 1.0 (es. 0.3, 0.4, 0.5, 0.8, 1.0). VIETATO usare numeri interi come 5 o 3 o lo zero.
            
            REGOLE TECNICHE TASSATIVE (PER IL PARSING):
            - Restituisci ESATTAMENTE un ARRAY JSON (es. [ {...}, {...} ]).
            - NON includere il campo "id" nella radice della domanda (sarà generato dal DB).
            - Usa "opt_1", "opt_2", "opt_3", "opt_4" come optionId.
            
            DEFINIZIONE DEGLI ARCHETIPI:
            %s
            
            FORMATO DI OUTPUT RICHIESTO:
            %s
            """.formatted(amount, slugsString, archetypesJson, converter.getFormat());

        Prompt prompt = new Prompt(systemPrompt, OpenAiChatOptions.builder().model("gpt-4o-mini").temperature(0.8).build());
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        List<ArchetypeQuestion> questions = converter.convert(response);
        if (questions != null) {
            // Sanitizzazione brutale: rimuoviamo ogni chiave allucinata dall'LLM prima di salvare
            questions.forEach(q -> q.getOptions().forEach(opt ->
                    opt.getArchetypeWeights().keySet().retainAll(allowedSlugs)
            ));
            questions.forEach(mongoTemplate::save);
        }
    }

    public void generateCulturalQuestionsBackground(int amount) {
        BeanOutputConverter<List<CulturalQuestion>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<CulturalQuestion>>() {}
        );

        String systemPrompt = """
            CONTESTO DELL'APP:
            "Play The City" è un'app di esplorazione urbana gamificata. Gli utenti girano per le città affrontando enigmi, scoprendo storie segrete e superando prove basate sui luoghi di interesse (arte, storia, architettura, leggende urbane).
            
            IL TUO COMPITO:
            Genera %d domande di cultura generale urbana, storica o architettonica per valutare il livello iniziale del giocatore durante l'onboarding in lingua: italiana. Queste domande fungeranno da vero e proprio quiz.
            
            REGOLE TASSATIVE:
            1. ID Domanda: Lascia "id" nullo (null).
            2. Opzioni: Crea 4 opzioni di risposta usando rigorosamente gli ID "opt_1", "opt_2", "opt_3", "opt_4".
            3. Risposta Corretta: Indica chiaramente l'ID dell'opzione giusta nel campo "correctOptionId".
            4. Difficoltà ("questionDifficulty"): Valuta la difficoltà della domanda assegnando 1 (Facile, cultura pop/generale), 2 (Media, richiede qualche conoscenza storica) o 3 (Difficile, per esperti d'arte/storia locale).
            
            FORMATO DI OUTPUT:
            %s
            """.formatted(amount, converter.getFormat());

        Prompt prompt = new Prompt(systemPrompt, OpenAiChatOptions.builder().model("gpt-4o-mini").temperature(0.7).build());
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        List<CulturalQuestion> questions = converter.convert(response);
        questions.forEach(mongoTemplate::save);
    }

    // =========================================================================
    // 2. FETCH ISTANTANEO PER L'UTENTE (Tempo: ~10ms)
    // =========================================================================

    /**
     * STEP 1: L'utente inizia l'onboarding.
     * Peschiamo le domande e salviamo il "puntatore" nel DB.
     */
    public OnboardingResponse startOnboarding() {
        // Pesca i campioni random
        List<ArchetypeQuestion> archQs = fetchRandom(ArchetypeQuestion.class, 5);
        List<CulturalQuestion> cultQs = fetchRandom(CulturalQuestion.class, 3);

        // Salva la sessione con i riferimenti (ID)
        OnboardingSession session = new OnboardingSession();
        session.setArchetypeQuestionIds(archQs.stream().map(ArchetypeQuestion::getId).toList());
        session.setCulturalQuestionIds(cultQs.stream().map(CulturalQuestion::getId).toList());
        mongoTemplate.save(session);

        // Ritorna le domande piene al frontend + l'ID sessione
        return new OnboardingResponse(session.getId(), archQs, cultQs);
    }

    /**
     * STEP 2: L'utente invia le risposte.
     * Usiamo l'ID sessione per recuperare le domande originali dal pool e calcolare.
     */
    public GameSession finalizeProfile(FinalizeRequest request) {
        OnboardingSession session = mongoTemplate.findById(request.sessionId(), OnboardingSession.class);

        // Recuperiamo le domande complete dal pool usando gli ID salvati nella sessione
        List<ArchetypeQuestion> archQs = fetchByIds(ArchetypeQuestion.class, session.getArchetypeQuestionIds());
        List<CulturalQuestion> cultQs = fetchByIds(CulturalQuestion.class, session.getCulturalQuestionIds());

        // Calcoliamo i risultati (usando la logica di prima)
        ProfileResult result = calculateUserResults(archQs, cultQs, request.answers());

        // STEP 3: CREIAMO LA SESSIONE DI GIOCO (Il tabellone)
        GameSession game = new GameSession();
        game.setArchetype(result.archetype());
        game.setDifficulty(result.difficulty());
        game.setCity(request.city());
        game.setLatitude(request.latitude());
        game.setLongitude(request.longitude());
        game.setAge(request.age());

        return mongoTemplate.save(game);
    }

    // Metodi helper per pulizia
    private <T> List<T> fetchRandom(Class<T> clazz, int size) {
        TypedAggregation<T> agg = Aggregation.newAggregation(clazz, Aggregation.sample(size));
        return mongoTemplate.aggregate(agg, clazz).getMappedResults();
    }

    private <T> List<T> fetchByIds(Class<T> clazz, List<String> ids) {
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(ids)), clazz);
    }

    // =========================================================================
    // 3. CALCOLO FINALE DEL PROFILO
    // =========================================================================

    public ProfileResult calculateUserResults(
            List<ArchetypeQuestion> askedArchQs,
            List<CulturalQuestion> askedCultQs,
            Map<String, String> allAnswers) {

        // 1. Calcolo Archetipo
        Map<String, Double> archetypeScores = new HashMap<>();
        for (ArchetypeQuestion q : askedArchQs) {
            String selectedOpt = allAnswers.get(q.getId()); // Usiamo allAnswers
            if (selectedOpt != null) {
                q.getOptions().stream()
                        .filter(opt -> opt.getOptionId().equals(selectedOpt))
                        .findFirst()
                        .ifPresent(opt -> opt.getArchetypeWeights().forEach((slug, weight) ->
                                archetypeScores.merge(slug, weight, Double::sum)
                        ));
            }
        }
        String dominantArchetype = archetypeScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("mystery-hunter"); // Fallback

        // 2. Calcolo Difficoltà (Vero Quiz di Cultura)
        int puntiOttenuti = 0;
        int puntiMassimiPossibili = 0;

        for (CulturalQuestion q : askedCultQs) {
            puntiMassimiPossibili += q.getQuestionDifficulty();

            String selectedOpt = allAnswers.get(q.getId()); // Usiamo allAnswers

            if (selectedOpt != null && selectedOpt.equals(q.getCorrectOptionId())) {
                puntiOttenuti += q.getQuestionDifficulty();
            }
        }

        double percentualeSuccesso = puntiMassimiPossibili > 0
                ? (double) puntiOttenuti / puntiMassimiPossibili
                : 0;

        String level;
        if (percentualeSuccesso < 0.4) {
            level = "Basso";
        } else if (percentualeSuccesso < 0.8) {
            level = "Medio";
        } else {
            level = "Alto";
        }

        return new ProfileResult(dominantArchetype, level);
    }

}