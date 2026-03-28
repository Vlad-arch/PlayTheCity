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
            // Spring Boot mappa AUTOMATICAMENTE il JSON nel tuo Record FoursquareResponse!
            FoursquareResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/places/search")
                            .queryParam("ll", coordinate)
                            .queryParam("radius", raggioMetri)
                            .queryParam("query", queryDalProfilo)
                            .queryParam("limit", 10)
                            .queryParam("fields", "fsq_place_id,name,categories,latitude,longitude")
                            .build())
                    .retrieve()
                    .body(FoursquareResponse.class); // <-- LA MAGIA È QUI

            if (response == null || response.results() == null) {
                return listaPoi;
            }

            for (FoursquareResult result : response.results()) {
                String categoria = "Luogo di interesse";
                if (result.categories() != null && !result.categories().isEmpty()) {
                    categoria = result.categories().get(0).name();
                }

                // Usiamo direttamente le variabili flat
                double poiLat = result.latitude();
                double poiLon = result.longitude();

                listaPoi.add(new FoursquarePoi(
                        result.fsqPlaceId(), // Aggiornato il getter
                        result.name(),
                        categoria,
                        poiLat,
                        poiLon
                ));
            }

            return listaPoi;

        } catch (Exception e) {
            System.err.println("Errore Foursquare: " + e.getMessage());
            return listaPoi;
        }
    }
}