package it.siali.playthecity.dto.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiSummaryResponse(
        String type,      // Ci serve per scartare le "disambiguation"
        String title,     // Il titolo ufficiale
        String extract,   // Il testo puro per l'LLM
        WikiThumbnail thumbnail // L'immagine (se esiste)
) {}