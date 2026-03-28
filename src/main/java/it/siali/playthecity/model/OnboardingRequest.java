package it.siali.playthecity.model;

import java.util.List;

public record OnboardingRequest(
        String citta,
        int eta,
        int numeroPersone, // 1 = in solitaria, >1 = gruppo
        int tempoMinuti,   // es. 120 per due ore
        List<String> interessi, // Gli swipe a destra delle card "Interessi"
        List<String> mood,      // Gli swipe a destra delle card "Mood"
        String campoLibero,     // "Voglio evitare i posti troppo affollati"
        int risposteEsatteQuiz  // Punteggio da 0 a 3 del quiz di profilazione
) {}