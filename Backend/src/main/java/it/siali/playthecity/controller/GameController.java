package it.siali.playthecity.controller;

import it.siali.playthecity.documets.Archetype;
import it.siali.playthecity.documets.OnboardingQuiz;
import it.siali.playthecity.dto.GameStartRequest;
import it.siali.playthecity.dto.GeneratedItinerary;
import it.siali.playthecity.dto.QuizSubmissionRequest;
import it.siali.playthecity.model.PlayerProfile;
import it.siali.playthecity.repository.ArchetypeRepository;
import it.siali.playthecity.service.AiCityGeneratorService;
import it.siali.playthecity.service.PersonalizationEngineService;
import it.siali.playthecity.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // FONDAMENTALE per far chiamare l'API da React Native (Expo)
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private PersonalizationEngineService personalizationService;

    @Autowired
    private AiCityGeneratorService aiCityGeneratorService;

    @Autowired
    private ArchetypeRepository archetypeRepository;

    @Autowired
    private QuizService quizService;

    @PostMapping("/start")
    public ResponseEntity<GeneratedItinerary> startGame(@RequestBody GameStartRequest request) {

        log.info("🚀 Richiesta ricevuta! Avvio generazione per lat: " + request.lat() + ", lon: " + request.lon());

        List<Archetype> archetypeList = archetypeRepository.findAll();


        try {
            // STEP 1: Profilazione (Traduciamo il form in parametri tecnici usando l'LLM)
            log.info("🧠 Generazione profilo utente in corso...");
            PlayerProfile profile = personalizationService.creaProfiloGiocatore(request.preferences());

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
    }

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
