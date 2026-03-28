package it.siali.playthecity.service;

import it.siali.playthecity.dto.CandidatePoi;
import it.siali.playthecity.dto.GeneratedItinerary;
import it.siali.playthecity.dto.foursquare.FoursquarePoi;
import it.siali.playthecity.model.PlayerProfile;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiCityGeneratorService {

    private final FoursquareService foursquareService;
    private final WikipediaBridgeService wikiService;
    // Usiamo ChatModel al posto di ChatClient!
    private final ChatModel chatModel;

    public AiCityGeneratorService(FoursquareService foursquareService,
                                  WikipediaBridgeService wikiService,
                                  ChatModel chatModel) {
        this.foursquareService = foursquareService;
        this.wikiService = wikiService;
        this.chatModel = chatModel;
    }

    public GeneratedItinerary creaItinerario(double lat, double lon, PlayerProfile profilo,
                                             int numFoursquare, int numWiki) {

        List<CandidatePoi> tuttiICandidati = new ArrayList<>();

        // 1. RECUPERO DA FOURSQUARE
        String queryFsq = profilo.queryFoursquare().isEmpty() ? "bar" : profilo.queryFoursquare().get(0);
        List<FoursquarePoi> fsqPois = foursquareService.cercaLuoghi(lat, lon, queryFsq, profilo.raggioEsplorazioneMetri());

        fsqPois.stream().limit(numFoursquare).forEach(poi ->
                tuttiICandidati.add(new CandidatePoi(poi.nome(), "FOURSQUARE", poi.categoria()))
        );

        // 2. RECUPERO DA WIKIPEDIA
        List<String> wikiNomi = wikiService.cercaMonumentiVicini(lat, lon, profilo.raggioEsplorazioneMetri());
        wikiNomi.stream().limit(numWiki).forEach(nome ->
                tuttiICandidati.add(new CandidatePoi(nome, "WIKIPEDIA", "Monumento storico/Culturale"))
        );

        // 3. LA SCELTA FINALE (RERANKING)
        return selezionaItinerarioIdeale(tuttiICandidati, profilo);
    }

    private GeneratedItinerary selezionaItinerarioIdeale(List<CandidatePoi> candidati, PlayerProfile profilo) {

        String listaCandidatiTesto = candidati.stream()
                .map(c -> "- " + c.name() + " (Tipo: " + c.categoryOrDescription() + ", Fonte: " + c.source() + ")")
                .collect(Collectors.joining("\n"));

        // Prepariamo il convertitore per forzare l'output in JSON verso il nostro Record
        BeanOutputConverter<GeneratedItinerary> converter = new BeanOutputConverter<>(GeneratedItinerary.class);
        String jsonFormatInstructions = converter.getFormat(); // Genera le istruzioni JSON per l'LLM

        String systemPromptTesto = String.format("""
            Sei il Game Master di un'app di esplorazione urbana.
            Il giocatore ha questo profilo:
            - Tono preferito: %s
            - Difficoltà quiz: %s
            
            Ecco una lista di luoghi candidati attorno a lui:
            %s
            
            Scegli ESATTAMENTE 3 luoghi da questa lista che creino un itinerario coerente.
            Crea un titolo per l'avventura e un messaggio di benvenuto.
            
            %s
            """,
                profilo.tonoVoceNarrante(), profilo.livelloDifficolta(), listaCandidatiTesto, jsonFormatInstructions
        );

        // ECCO IL TUO AMATO PROMPT CON LE OPZIONI SPECIFICHE!
        Prompt prompt = new Prompt(
                systemPromptTesto,
                OpenAiChatOptions.builder()
                        .model("gpt-5-mini") // Specifica il modello Regolo qui
                        .temperature(1.0)               // Un pizzico di creatività
                        .build()
        );

        // Chiamata sincrona all'LLM
        ChatResponse response = chatModel.call(prompt);

        // Estrazione del testo JSON restituito
        String testoRisposta = response.getResult().getOutput().getText();

        // Conversione magica dalla stringa JSON al nostro Record Java
        return converter.convert(testoRisposta);
    }
}