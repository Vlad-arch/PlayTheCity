package it.siali.playthecity.controller;

import it.siali.playthecity.documets.Archetype;
import it.siali.playthecity.documets.GameSession;
import it.siali.playthecity.documets.OnboardingQuiz;
import it.siali.playthecity.dto.*;
import it.siali.playthecity.model.PlayerProfile;
import it.siali.playthecity.repository.ArchetypeRepository;
import it.siali.playthecity.service.AiCityGeneratorService;
import it.siali.playthecity.service.DynamicGameEngineService;
import it.siali.playthecity.service.PersonalizationEngineService;
import it.siali.playthecity.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // FONDAMENTALE per far chiamare l'API da React Native (Expo)
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private PersonalizationEngineService personalizationEngineService;

    @Autowired
    private DynamicGameEngineService dynamicGameEngineService;

    @Autowired
    private AiCityGeneratorService aiCityGeneratorService;

    @Autowired
    private ArchetypeRepository archetypeRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private MongoTemplate mongoTemplate;

    // La chiave segreta per proteggere le rotte admin (impostabile in application.properties)
    // Se non la imposti, usa "super-secret-gunpowder" di default
    @Value("${admin.secret.key:super-secret-gunpowder}")
    private String expectedAdminKey;

    // =========================================================================
    // ROTTE ADMIN (Protette da Header X-Admin-Key)
    // =========================================================================

    @PostMapping("/admin/generate/archetypes")
    public ResponseEntity<String> generateArchetypeQuestions(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @RequestBody AdminArchetypeRequest request) {

        if (!expectedAdminKey.equals(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non sei autorizzato.");
        }

        try {
            // Chiamata asincrona o sincrona a seconda di come l'hai configurata
            List<Archetype> archetypes = archetypeRepository.findAll();
            personalizationEngineService.generateArchetypeQuestionsBackground(archetypes, request.amount());
            return ResponseEntity.ok("Generazione di " + request.amount() + " domande comportamentali completata.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore LLM: " + e.getMessage());
        }
    }

    @PostMapping("/admin/generate/cultural")
    public ResponseEntity<String> generateCulturalQuestions(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @RequestParam(defaultValue = "10") int amount) {

        if (!expectedAdminKey.equals(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non sei autorizzato.");
        }

        try {
            personalizationEngineService.generateCulturalQuestionsBackground(amount);
            return ResponseEntity.ok("Generazione di " + amount + " domande culturali completata.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore LLM: " + e.getMessage());
        }
    }

    // =========================================================================
    // ROTTE DEL GIOCO (Pubbliche, per il Frontend)
    // =========================================================================

    /**
     * Chiamata dal frontend quando l'utente clicca su "Inizia"
     * Crea la sessione e restituisce le domande pescate dal pool.
     */
    @PostMapping("/onboarding/start")
    public ResponseEntity<OnboardingResponse> startOnboarding() {
        try {
            OnboardingResponse response = personalizationEngineService.startOnboarding();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Chiamata dal frontend alla fine del questionario a step.
     * Riceve le risposte, calcola il profilo e crea il tabellone (GameSession).
     */
    @PostMapping("/onboarding/finalize")
    public ResponseEntity<GameSession> finalizeProfile(@RequestBody FinalizeRequest request) {
        try {
            // Genera la partita e il profilo!
            GameSession session = personalizationEngineService.finalizeProfile(request);
            Archetype archetype = archetypeRepository.findBySlug(session.getArchetype()).orElseThrow();
            GeneratedItinerary itinerary = aiCityGeneratorService.creaItinerario(
                    session,
                    archetype,
                    1,
                    30
            );

            FullAdventure adventure = dynamicGameEngineService.generateFullExperience(itinerary, archetype, request.city());
            List<GameSession.GameStep> persistentSteps = new ArrayList<>();
            for (int i = 0; i < adventure.steps().size(); i++) {
                var content = adventure.steps().get(i);
                var waypoint = itinerary.waypoints().get(i); // Per lat/lon e foto originali

                persistentSteps.add(new GameSession.GameStep(
                        content.locationName(),    // 1. locationName
                        waypoint.lat(),            // 2. lat
                        waypoint.lon(),            // 3. lon
                        waypoint.selectionReason(), // 4. description (usiamo la motivazione dell'AI come descrizione breve)
                        waypoint.imageUrl(),       // 5. imageUrl
                        content.narrativeStory(),  // 6. narrative (lo storytelling dell'archetipo)
                        content.quiz(),            // 7. quiz (l'oggetto GameQuiz completo)
                        waypoint.source()          // 8. source ("WIKIPEDIA" o "FOURSQUARE")
                ));
            }

            session.setAdventureTitle(adventure.adventureTitle());
            session.setOverallNarrative(adventure.overallNarrative());
            session.setSteps(persistentSteps);

            return ResponseEntity.ok(mongoTemplate.save(session));
        } catch (RuntimeException e) {
            // Es: "Sessione scaduta o non valida"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*@PostMapping("/start")
    public ResponseEntity<GeneratedItinerary> startGame(@RequestBody GameStartRequest request) {

        log.info("🚀 Richiesta ricevuta! Avvio generazione per lat: " + request.lat() + ", lon: " + request.lon());

        List<Archetype> archetypeList = archetypeRepository.findAll();


        try {
            // STEP 1: Profilazione (Traduciamo il form in parametri tecnici usando l'LLM)
            log.info("🧠 Generazione profilo utente in corso...");
            PlayerProfile profile = null;//personalizationEngineService.creaProfiloGiocatore(request.preferences());

            log.info("✅ Profilo generato. Tono: " + profile.tonoVoceNarrante() + " | Query: " + profile.queryFoursquare());

            // STEP 2: Orchestrazione (Recupero dati da Wiki/Foursquare e scelta finale dell'LLM)
            log.info("🌍 Ricerca luoghi e creazione itinerario (Reranking)...");

            // Passiamo 10 luoghi da Foursquare e 10 da Wikipedia da far valutare all'LLM
            GeneratedItinerary itinerary = aiCityGeneratorService.creaItinerario(
                    request.lat(),
                    request.lon(),
                    profile,
                    10,
                    10
            );

            log.info("🎉 Itinerario creato con successo: " + itinerary.adventureTitle());

            // STEP 3: Restituiamo il JSON perfetto al Frontend
            return ResponseEntity.ok(itinerary);

        } catch (Exception e) {
            System.err.println("❌ Errore critico durante la generazione: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }*/

    @GetMapping("/onboardingQuiz")
    public ResponseEntity<OnboardingQuiz> generateOnboardingQuiz() {
        List<Archetype> archetypeList = archetypeRepository.findAll();
        OnboardingQuiz onboardingQuiz = quizService.generateOnboardingQuiz(archetypeList);
        return ResponseEntity.ok(onboardingQuiz);
    }

    @PostMapping("/calculateDominantArchetype")
    public ResponseEntity<Archetype> calculateDominantArchetype(@RequestBody QuizSubmissionRequest request) {

        String slug = quizService.calculateDominantArchetype(request.answers(), request.quizId());

        Archetype archetype = archetypeRepository.findBySlug(slug).orElseThrow();
        return ResponseEntity.ok(archetype);
    }


}
