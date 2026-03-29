package it.siali.playthecity.dto.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiGeoResult(
        String title,
        double lat,  // Aggiungi questo
        double lon,  // Aggiungi questo
        double dist
) {}