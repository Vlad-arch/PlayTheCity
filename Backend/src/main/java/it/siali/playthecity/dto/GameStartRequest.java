package it.siali.playthecity.dto;

import it.siali.playthecity.model.OnboardingRequest;

public record GameStartRequest(
        double lat,
        double lon,
        OnboardingRequest preferences // Il Record che avevamo creato per il quiz/swipe
) {}