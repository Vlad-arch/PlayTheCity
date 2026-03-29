package it.siali.playthecity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandidatePoi(
        String name,
        String description,
        String imageUrl,
        double lat,
        double lon,
        String source,               // "FOURSQUARE" o "WIKIPEDIA"
        String categoryOrDescription // La categoria (es: "Murales") o un'etichetta breve
) {}