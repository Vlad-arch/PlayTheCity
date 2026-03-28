package it.siali.playthecity.dto;

import java.util.List;

public record GeneratedItinerary(
        String adventureTitle,
        String welcomeMessage,
        List<ItineraryWaypoint> waypoints // Le "tappe" del viaggio
) {}
