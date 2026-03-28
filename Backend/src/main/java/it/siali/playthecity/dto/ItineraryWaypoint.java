package it.siali.playthecity.dto;

public record ItineraryWaypoint(
        String locationName,
        String selectionReason, // Perché l'ha scelto in base al profilo
        String originalSource   // Per sapere a chi chiedere i dettagli dopo
) {}