package it.siali.playthecity.dto;

public record CandidatePoi(
        String name,
        String source, // "FOURSQUARE", "WIKIPEDIA", "REGOLO_AI"
        String categoryOrDescription
) {}