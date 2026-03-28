import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class WikipediaBridgeService {

    private final RestClient actionClient;
    private final RestClient restClient;

    public WikipediaBridgeService() {
        // Client per la ricerca
        this.actionClient = RestClient.builder()
                .baseUrl("https://it.wikipedia.org/w/api.php")
                .defaultHeader("User-Agent", "PlayTheCityHackathon/1.0")
                .build();

        // Client per scaricare il testo
        this.restClient = RestClient.builder()
                .baseUrl("https://it.wikipedia.org/api/rest_v1")
                .defaultHeader("User-Agent", "PlayTheCityHackathon/1.0")
                .build();
    }

    // Passagli il nome da Foursquare (es. "Caffè Greco") e la città (es. "Roma")
    public String recuperaStoriaPerRAG(String nomeLuogoFoursquare, String citta) {
        try {
            // 1. CERCA IL TITOLO ESATTO
            String queryRicerca = nomeLuogoFoursquare + " " + citta;

            JsonNode searchResponse = actionClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("list", "search")
                            .queryParam("srsearch", queryRicerca)
                            .queryParam("utf8", "")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode searchResults = searchResponse.path("query").path("search");

            // Se Wikipedia non trova nulla (array vuoto), andiamo al fallback
            if (searchResults.isEmpty()) {
                return modalitaFantasiaFallback(nomeLuogoFoursquare);
            }

            // Prendi il titolo del primo risultato
            String titoloUfficiale = searchResults.get(0).path("title").asText();

            // 2. SCARICA IL TESTO ESTRATTO
            JsonNode summaryResponse = restClient.get()
                    .uri("/page/summary/{titolo}", titoloUfficiale)
                    .retrieve()
                    .body(JsonNode.class);

            // Controlla che non sia una pagina di disambiguazione
            if ("disambiguation".equals(summaryResponse.path("type").asText())) {
                return modalitaFantasiaFallback(nomeLuogoFoursquare);
            }

            return summaryResponse.path("extract").asText();

        } catch (Exception e) {
            // Se qualsiasi API va in timeout o errore, NON crashare l'app!
            System.out.println("Errore Wiki per " + nomeLuogoFoursquare + ". Attivo fallback.");
            return modalitaFantasiaFallback(nomeLuogoFoursquare);
        }
    }

    // IL SALVAVITA DELL'HACKATHON
    private String modalitaFantasiaFallback(String nomeLuogo) {
        return "Nessun dato storico trovato per " + nomeLuogo + ". Genera una leggenda urbana inventata, intrigante e misteriosa su questo luogo, specificando al giocatore che si tratta di una diceria locale.";
    }
}