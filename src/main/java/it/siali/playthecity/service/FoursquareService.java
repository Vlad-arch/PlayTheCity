package it.siali.playthecity.service;

import it.siali.playthecity.dto.Foursquare.FoursquarePoi;
import it.siali.playthecity.dto.Foursquare.FoursquareResponse;
import it.siali.playthecity.dto.Foursquare.FoursquareResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoursquareService {

    private final RestClient restClient;

    public FoursquareService(@Value("${foursquare.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.foursquare.com/v3")
                .defaultHeader("Authorization", apiKey)
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
                            .queryParam("limit", 20)
                            .queryParam("fields", "fsq_id,name,categories,geocodes")
                            .build())
                    .retrieve()
                    .body(FoursquareResponse.class); // <-- LA MAGIA È QUI

            // Se la risposta è vuota, torniamo la lista vuota
            if (response == null || response.results() == null) {
                return listaPoi;
            }

            // Trasformiamo i dati grezzi di Foursquare nel nostro DTO pulito per l'LLM
            for (FoursquareResult result : response.results()) {

                String categoria = "Luogo di interesse";
                if (result.categories() != null && !result.categories().isEmpty()) {
                    categoria = result.categories().get(0).name();
                }

                double poiLat = result.geocodes().main().latitude();
                double poiLon = result.geocodes().main().longitude();

                listaPoi.add(new FoursquarePoi(
                        result.fsqId(),
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