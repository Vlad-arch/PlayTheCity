package it.siali.playthecity.dto.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquarePhoto(
        String id,          // Identificativo univoco della foto
        String prefix,      // Esempio: "https://fastly.4sqi.net/img/general/"
        String suffix,      // Esempio: "/717_mS_no_8bWp-99.jpg"
        int width,          // Larghezza originale
        int height,         // Altezza originale
        String created_at   // Data di creazione
) {}