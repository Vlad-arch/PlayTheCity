package it.siali.playthecity.service;
import it.siali.playthecity.dto.CandidatePoi;
import it.siali.playthecity.dto.GeneratedItinerary;
import it.siali.playthecity.dto.foursquare.FoursquarePoi;
import it.siali.playthecity.model.PlayerProfile;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameOrchestratorService {

    @Autowired
    private FoursquareService foursquareService;
    @Autowired
    private  WikipediaBridgeService wikiService;
    @Autowired
    private ChatClient chatClient; // Questo punterà a Regolo.ai o OpenAI


    public GeneratedItinerary creaItinerario(double lat, double lon, PlayerProfile profilo,
                                             int numFoursquare, int numWiki) {

        List<CandidatePoi> tuttiICandidati = new ArrayList<>();

        // 1. RECUPERO DA FOURSQUARE (Lifestyle, Bar, Parchi)
        // Usiamo la prima query generata dal profilo (es. "speakeasy")
        String queryFsq = profilo.queryFoursquare().isEmpty() ? "attrazioni" : profilo.queryFoursquare().get(0);
        List<FoursquarePoi> fsqPois = foursquareService.cercaLuoghi(lat, lon, queryFsq, profilo.raggioEsplorazioneMetri());

        // Prendiamo solo il numero richiesto e li convertiamo nel formato generico
        fsqPois.stream().limit(numFoursquare).forEach(poi ->
                tuttiICandidati.add(new CandidatePoi(poi.nome(), "FOURSQUARE", poi.categoria()))
        );

        // 2. RECUPERO DA WIKIPEDIA (Monumenti storici via GeoSearch)
        // Nota: Assumo che tu abbia aggiunto un metdo cercaMonumentiVicini(lat, lon, raggio) in WikipediaBridgeService
        List<String> wikiNomi = wikiService.cercaMonumentiVicini(lat, lon, profilo.raggioEsplorazioneMetri());
        wikiNomi.stream().limit(numWiki).forEach(name ->
                tuttiICandidati.add(new CandidatePoi(name, "WIKIPEDIA", "Monumento storico/Culturale"))
        );

        // 3. RECUPERO DA REGOLO.AI / LLM (Gemme Nascoste - Opzionale)
        try {
            String promptGemme = String.format("Dimmi il nome di 2 luoghi insoliti e segreti vicino a lat: %f, lon: %f. Rispondi SOLO con i nomi separati da virgola.", lat, lon);
            String rispostaLlm = chatClient.prompt().user(promptGemme).call().content();
            for (String gemma : rispostaLlm.split(",")) {
                tuttiICandidati.add(new CandidatePoi(gemma.trim(), "REGOLO_AI", "Gemma Nascosta"));
            }
        } catch (Exception e) {
            System.out.println("Salto le gemme nascoste, LLM in timeout.");
        }

        // 4. LA SCELTA FINALE (RERANKING)
        return selezionaItinerarioIdeale(tuttiICandidati, profilo);
    }

    private GeneratedItinerary selezionaItinerarioIdeale(List<CandidatePoi> candidati, PlayerProfile profilo) {

        // Trasformiamo la lista dei candidati in una stringa leggibile per il prompt
        String listaCandidatiTesto = candidati.stream()
                .map(c -> "- " + c.name() + " (Tipo: " + c.categoryOrDescription() + ", Fonte: " + c.source() + ")")
                .collect(Collectors.joining("\n"));

        String systemPrompt = String.format("""
            Sei il Game Master di un'app di esplorazione urbana.
            Il giocatore ha questo profilo:
            - Tono preferito: %s
            - Difficoltà quiz: %s
            
            Ecco una lista di luoghi candidati attorno a lui:
            %s
            
            Scegli ESATTAMENTE 3 luoghi da questa lista che creino un itinerario coerente e avvincente.
            Crea un titolo per l'avventura e un messaggio di benvenuto.
            """,
                profilo.tonoVoceNarrante(), profilo.livelloDifficolta(), listaCandidatiTesto
        );

        // Magia di Spring AI: Restituisce direttamente il record GeneratedItinerary!
        return chatClient.prompt()
                .system(systemPrompt)
                .user("Genera l'itinerario perfetto in formato JSON strutturato.")
                .call()
                .entity(GeneratedItinerary.class);
    }
}