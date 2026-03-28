package it.siali.playthecity.model;

import java.util.List;

public record PlayerProfile(

        List<String> queryFoursquare, // Max 3 query testuali esatte per cercare i luoghi
        String tonoVoceNarrante,      // Come ElevenLabs dovrà leggere la storia
        String livelloDifficolta,     // "facile", "medio", "difficile" per i quiz futuri
        int raggioEsplorazioneMetri   // Calcolato in base al tempo
) { }