package it.siali.playthecity.service;

import it.siali.playthecity.dto.foursquare.FoursquarePoi;
import it.siali.playthecity.dto.foursquare.FoursquareResponse;
import it.siali.playthecity.dto.foursquare.FoursquareResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoursquareService {

    private final RestClient restClient;

    public FoursquareService(@Value("${foursquare.api.key}") String apiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 secondi per connettersi a Foursquare
        factory.setReadTimeout(15000);   // 10 secondi per aspettare la risposta (Risolve il Netty Timeout!)

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://places-api.foursquare.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("X-Places-Api-Version", " 2025-06-17")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public List<FoursquarePoi> cercaLuoghi(double lat, double lon, String queryDalProfilo, int raggioMetri) {
        List<FoursquarePoi> listaPoi = new ArrayList<>();
        String coordinate = lat + "," + lon;

        try {
            FoursquareResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/places/search")
                            .queryParam("ll", coordinate)
                            .queryParam("radius", raggioMetri)
                            .queryParam("query", queryDalProfilo)
                            .queryParam("limit", 10)
                            // Assicuriamoci che i campi siano esattamente quelli del JSON Foursquare
                            .queryParam("fields", "fsq_place_id,name,categories,latitude,longitude,description,photos")
                            .build())
                    .retrieve()
                    .body(FoursquareResponse.class);

            if (response == null || response.results() == null) {
                return listaPoi;
            }

            for (FoursquareResult result : response.results()) {
                // 1. Estrazione Categoria
                String categoria = "Luogo di interesse";
                if (result.categories() != null && !result.categories().isEmpty()) {
                    categoria = result.categories().get(0).name();
                }

                // 2. Estrazione Descrizione (con fallback)
                String desc = (result.description() != null && !result.description().isBlank())
                        ? result.description()
                        : "Scopri i segreti di questo luogo durante la tua avventura.";

                // 3. Costruzione URL Foto (Foursquare usa prefix + size + suffix)
                String imgUrl = "https://via.placeholder.com/400x300?text=Immagine+non+disponibile";
                if (result.photos() != null && !result.photos().isEmpty()) {
                    var photo = result.photos().get(0);
                    // "original" o "600x400" sono dimensioni valide
                    imgUrl = photo.prefix() + "original" + photo.suffix();
                }

                // 4. CREAZIONE RECORD (Ora con tutti i 7 parametri richiesti)
                listaPoi.add(new FoursquarePoi(
                        result.fsqPlaceId(),   // 1. id
                        result.name(),         // 2. nome
                        categoria,             // 3. categoria
                        result.latitude(),     // 4. lat
                        result.longitude(),    // 5. lon
                        desc,                  // 6. descrizione
                        imgUrl                 // 7. imageUrl
                ));
            }

            return listaPoi;

        } catch (Exception e) {
            System.err.println("Errore Foursquare: " + e.getMessage());
            e.printStackTrace(); // Utile in fase di hackathon per vedere l'errore esatto
            return listaPoi;
        }
    }
}