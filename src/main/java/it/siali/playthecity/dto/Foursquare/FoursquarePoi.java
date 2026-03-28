package it.siali.playthecity.dto.Foursquare;

public record FoursquarePoi(
        String id,          // L'ID univoco di Foursquare (fsq_id)
        String nome,        // Il nome del locale/parco
        String categoria,   // La categoria principale (es. "Speakeasy")
        double lat,         // Latitudine per React Native Maps
        double lon          // Longitudine per React Native Maps
) {}
