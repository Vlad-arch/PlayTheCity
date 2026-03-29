package it.siali.playthecity.service;

import it.siali.playthecity.documets.Archetype;
import it.siali.playthecity.documets.GameSession;
import it.siali.playthecity.dto.CandidatePoi;
import it.siali.playthecity.dto.GeneratedItinerary;
import it.siali.playthecity.dto.SelectedWaypoint;
import it.siali.playthecity.dto.foursquare.FoursquarePoi;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AiCityGeneratorService {

    private final FoursquareService foursquareService;
    private final WikipediaBridgeService wikiService;
    private final ChatModel chatModel;

    public AiCityGeneratorService(FoursquareService foursquareService,
                                  WikipediaBridgeService wikiService,
                                  ChatModel chatModel) {
        this.foursquareService = foursquareService;
        this.wikiService = wikiService;
        this.chatModel = chatModel;
    }

    /**
     * Recupera i POI dalle API e delega all'AI la scelta dei 3 migliori.
     */
    public GeneratedItinerary creaItinerario(GameSession session, Archetype archetype, int numFoursquare, int numWiki) {
        List<CandidatePoi> tuttiICandidati = new ArrayList<>();

        // 1. RECUPERO DA FOURSQUARE (Lifestyle/Trending)
        String queryFsq = String.join(", ", archetype.getGameplayLogic().getPreferredPoiTypes());
        if (queryFsq.isEmpty()) queryFsq = "trending";

        List<FoursquarePoi> fsqPois = foursquareService.cercaLuoghi(
                Double.parseDouble(session.getLatitude()),
                Double.parseDouble(session.getLongitude()),
                queryFsq,
                2000
        );

        // Mappiamo includendo Foto, Descrizione e Coordinate
        fsqPois.stream().limit(numFoursquare).forEach(poi ->
                tuttiICandidati.add(new CandidatePoi(
                        poi.nome(),
                        poi.descrizione(),
                        poi.imageUrl(),
                        poi.lat(),
                        poi.lon(),
                        "FOURSQUARE",
                        poi.categoria()
                ))
        );

        // 2. RECUPERO DA WIKIPEDIA (GeoSearch)
        // Nota: Assumiamo che wikiService ora restituisca oggetti CandidatePoi o simili per avere lat/lon
        List<CandidatePoi> wikiPois = wikiService.cercaMonumentiViciniDettagliati(
                Double.parseDouble(session.getLatitude()),
                Double.parseDouble(session.getLongitude()),
                2000,
                numWiki
        );

        wikiPois.stream().limit(numWiki).forEach(tuttiICandidati::add);

        // 3. RERANKING AI
        GeneratedItinerary itinerarioScelto = selezionaItinerarioIdeale(tuttiICandidati, archetype);

        // 4. RICONCILIAZIONE DATI (Fondamentale!)
        // L'AI restituisce solo nomi e motivi. Noi riattacchiamo lat/lon/img originali.
        return arricchisciItinerarioConDatiOriginali(itinerarioScelto, tuttiICandidati);
    }

    private GeneratedItinerary selezionaItinerarioIdeale(List<CandidatePoi> candidati, Archetype archetype) {
        // 1. Gestione di emergenza: se non ci sono candidati, non chiamiamo nemmeno l'AI
        if (candidati == null || candidati.isEmpty()) {
            return new GeneratedItinerary("Avventura in corso", "Esplora la città!", new ArrayList<>());
        }

        String listaCandidatiTesto = candidati.stream()
                .map(c -> "- " + c.name() + " (Categoria: " + c.categoryOrDescription() + ", Fonte: " + c.source() + ")")
                .collect(Collectors.joining("\n"));

        BeanOutputConverter<GeneratedItinerary> converter = new BeanOutputConverter<>(GeneratedItinerary.class);

        // 2. Prompt modificato per essere "Real-World Constrained"
        String systemPromptTesto = String.format("""
            Sei il Game Master di 'Play The City'. Il giocatore è un: %s (%s).
            
            TUA PERSONA:
            %s
            Tono: %s.
            Keywords: %s.
            
            LOGICA:
            Tipi preferiti: %s.
            Moltiplicatore difficoltà: %.1f.
            
            LUOGHI CANDIDATI REALI (USA SOLO QUESTI):
            %s
            
            TASK:
            1. Scegli i migliori luoghi dalla lista fornita che siano coerenti con l'archetipo %s.
            2. REGOLE STRINGENTI SULLA QUANTITÀ:
               - Se ci sono più di 6 candidati, scegline esattamente 6.
               - Se ci sono tra 1 e 6 candidati, selezionali TUTTI.
               - NON inventare MAI luoghi non presenti nella lista fornita. Se la lista è corta, adattati.
               - Se non ci sono luoghi perfettamente coerenti, scegli comunque i più vicini o interessanti dalla lista.
            3. Crea un titolo e un messaggio di benvenuto coerenti con l'archetipo.
            
            %s
            """,
                archetype.getDisplayName(), archetype.getTagline(),
                archetype.getNarrativeEngineConfig().getAiPersonaPrompt(),
                archetype.getNarrativeEngineConfig().getToneOfVoice(),
                String.join(", ", archetype.getNarrativeEngineConfig().getVocabularyKeywords()),
                String.join(", ", archetype.getGameplayLogic().getPreferredPoiTypes()),
                archetype.getGameplayLogic().getDifficultyMultiplier(),
                listaCandidatiTesto,
                archetype.getSlug(),
                converter.getFormat()
        );

        Prompt prompt = new Prompt(
                systemPromptTesto,
                OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.7) // Abbassata leggermente la temperatura per ridurre le allucinazioni
                        .build()
        );

        try {
            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();
            return converter.convert(content);
        } catch (Exception e) {
            // Fallback estremo: se l'AI fallisce il JSON, creiamo un itinerario base con quello che abbiamo
            List<SelectedWaypoint> fallbackWaypoints = candidati.stream()
                    .limit(6)
                    .map(c -> new SelectedWaypoint(c.name(), "Un luogo misterioso da scoprire.", c.imageUrl(), c.lat(), c.lon(), c.source(), c.description()))
                    .collect(Collectors.toList());
            return new GeneratedItinerary("Esplorazione Urbana", "La tua avventura inizia qui.", fallbackWaypoints);
        }
    }

    /**
     * Prende l'itinerario generato dall'AI e reinserisce le coordinate e le immagini
     * trovate originariamente dai servizi Foursquare/Wikipedia.
     */
    private GeneratedItinerary arricchisciItinerarioConDatiOriginali(GeneratedItinerary itinerary, List<CandidatePoi> originali) {
        List<SelectedWaypoint> arricchiti = itinerary.waypoints().stream()
                .map(selected -> {
                    return originali.stream()
                            .filter(c -> c.name().equalsIgnoreCase(selected.locationName()))
                            .findFirst()
                            .map(match -> new SelectedWaypoint(
                                    selected.locationName(),
                                    selected.selectionReason(),
                                    match.imageUrl(),
                                    match.lat(),
                                    match.lon(),
                                    match.source(),
                                    match.description()
                            )).orElse(null); // Ritorna null se l'AI lo ha inventato
                })
                .filter(Objects::nonNull) // <--- RIMUOVE I LUOGHI INVENTATI
                .collect(Collectors.toList());

        return new GeneratedItinerary(itinerary.adventureTitle(), itinerary.welcomeMessage(), arricchiti);
    }
}