package it.siali.playthecity.service;

import it.siali.playthecity.dto.wikipedia.WikiGeoResult;
import it.siali.playthecity.dto.wikipedia.WikiGeoSearchResponse;
import it.siali.playthecity.dto.wikipedia.WikiSearchResponse;
import it.siali.playthecity.dto.wikipedia.WikiSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WikipediaBridgeService {

    private final RestClient actionClient;
    private final RestClient restClient;

    public WikipediaBridgeService() {
        this.actionClient = RestClient.builder()
                .baseUrl("https://it.wikipedia.org/w/api.php")
                .defaultHeader("User-Agent", "PlayTheCityHackathonBot/1.0 (test@example.com)")
                .build();

        this.restClient = RestClient.builder()
                .baseUrl("https://it.wikipedia.org/api/rest_v1")
                .defaultHeader("User-Agent", "PlayTheCityHackathonBot/1.0 (test@example.com)")
                .build();
    }

    // Piccolo record interno per restituire sia il testo che l'immagine
    public record StoriaWikiDTO(String testo, String imageUrl) {}

    public StoriaWikiDTO recuperaStoriaPerRAG(String nomeLuogoFoursquare, String citta) {
        try {
            // 1. CERCA IL TITOLO ESATTO
            String queryRicerca = nomeLuogoFoursquare + " " + citta;

            WikiSearchResponse searchResponse = actionClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("list", "search")
                            .queryParam("srsearch", queryRicerca)
                            .queryParam("utf8", "")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(WikiSearchResponse.class); // Mappatura automatica!

            // Controlli di sicurezza puliti
            if (searchResponse == null || searchResponse.query() == null || searchResponse.query().search().isEmpty()) {
                return modalitaFantasiaFallback(nomeLuogoFoursquare);
            }

            String titoloUfficiale = searchResponse.query().search().get(0).title();

            // 2. SCARICA IL TESTO E L'IMMAGINE
            WikiSummaryResponse summaryResponse = restClient.get()
                    .uri("/page/summary/{titolo}", titoloUfficiale)
                    .retrieve()
                    .body(WikiSummaryResponse.class); // Mappatura automatica!

            if (summaryResponse == null || "disambiguation".equals(summaryResponse.type())) {
                return modalitaFantasiaFallback(nomeLuogoFoursquare);
            }

            // Estraiamo l'immagine in modo sicuro (potrebbe essere null se la pagina Wiki non ha foto)
            String imageUrl = null;
            if (summaryResponse.thumbnail() != null) {
                imageUrl = summaryResponse.thumbnail().source();
            }

            return new StoriaWikiDTO(summaryResponse.extract(), imageUrl);

        } catch (Exception e) {
            System.err.println("Errore Wiki per " + nomeLuogoFoursquare + ": " + e.getMessage());
            return modalitaFantasiaFallback(nomeLuogoFoursquare);
        }
    }

    // IL SALVAVITA
    private StoriaWikiDTO modalitaFantasiaFallback(String nomeLuogo) {
        String testoInventato = "Nessun dato storico ufficiale per " + nomeLuogo + ". Genera una leggenda urbana inventata, intrigante e misteriosa su questo luogo, specificando al giocatore che si tratta di una diceria locale.";
        // Mettiamo un'immagine di default (es. un'icona misteriosa o un logo del vostro gioco) se manca Wiki
        String defaultImage = "https://via.placeholder.com/400x300?text=Luogo+Misterioso";
        return new StoriaWikiDTO(testoInventato, defaultImage);
    }

    public List<String> cercaMonumentiVicini(double lat, double lon, int raggioMetri) {
        // L'API di Wiki accetta max 10.000 metri
        int raggioSicuro = Math.min(raggioMetri, 10000);
        String coordinate = lat + "|" + lon; // Wiki vuole il pipe "|" tra lat e lon

        try {
            WikiGeoSearchResponse response = actionClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("list", "geosearch")
                            .queryParam("gsradius", raggioSicuro)
                            .queryParam("gscoord", coordinate)
                            .queryParam("gslimit", 10) // Prendiamone 10 tra cui l'LLM potrà scegliere
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(WikiGeoSearchResponse.class);

            // Controlli anti-nullpointer
            if (response == null || response.query() == null || response.query().geosearch() == null) {
                return List.of(); // Restituisce lista vuota se non trova nulla
            }

            // Estraiamo solo i titoli delle pagine Wiki trovate nei dintorni
            return response.query().geosearch().stream()
                    .map(WikiGeoResult::title)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Errore durante la GeoSearch di Wikipedia: " + e.getMessage());
            return List.of(); // Non facciamo crashare nulla!
        }
    }
}