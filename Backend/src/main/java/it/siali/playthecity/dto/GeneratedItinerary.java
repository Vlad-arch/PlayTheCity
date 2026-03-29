package it.siali.playthecity.dto;

import java.util.List;

public record GeneratedItinerary(
        String adventureTitle,
        String welcomeMessage,
        List<SelectedWaypoint> waypoints // <-- Deve essere SelectedWaypoint, non ItineraryWaypoint
) {}
