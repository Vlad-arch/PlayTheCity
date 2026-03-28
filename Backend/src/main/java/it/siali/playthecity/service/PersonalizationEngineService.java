package it.siali.playthecity.service;

import it.siali.playthecity.model.OnboardingRequest;
import it.siali.playthecity.model.PlayerProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PersonalizationEngineService {
    public PlayerProfile creaProfiloGiocatore(OnboardingRequest preferences) {
        return new PlayerProfile(new ArrayList<>(), "dolce", "medio", 2000);
    }
}
