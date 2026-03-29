package it.siali.playthecity.dto.foursquare;

public record FoursquarePoi(
        String id,
        String nome,
        String categoria,
        double lat,
        double lon,
        String descrizione, // <--- Controlla che si chiami così
        String imageUrl    // <--- Controlla che si chiami così
) {}