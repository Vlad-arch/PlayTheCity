package it.siali.playthecity.service;

import it.siali.playthecity.documets.Archetype;
import it.siali.playthecity.dto.FullAdventure;
import it.siali.playthecity.dto.GameStepContent;
import it.siali.playthecity.dto.GeneratedItinerary;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicGameEngineService {

    @Autowired
    private WikipediaBridgeService wikiService;
    @Autowired
    private ChatModel chatModel;

    public FullAdventure generateFullExperience(GeneratedItinerary itinerary, Archetype archetype, String city) {
        List<GameStepContent> stepsContent = new ArrayList<>();

        for (var waypoint : itinerary.waypoints()) {
            // 1. Recupero dati reali (Wiki o Foursquare - qui semplificato su Wiki)
            var wikiData = wikiService.recuperaStoriaPerRAG(waypoint.locationName(), city);

            // 2. Chiamata LLM per trasformare il testo in gioco
            stepsContent.add(generateStepDetails(waypoint.locationName(), wikiData.testo(), archetype));
        }

        return new FullAdventure(itinerary.adventureTitle(), itinerary.welcomeMessage(), stepsContent);
    }

    private GameStepContent generateStepDetails(String name, String context, Archetype archetype) {
        BeanOutputConverter<GameStepContent> converter = new BeanOutputConverter<>(GameStepContent.class);

        String systemPromptTesto = String.format("""
            Sei il Narrative Designer dell'archetipo: %s.
            
            CONTESTO REALE (WIKIPEDIA):
            %s
            
            TASK:
            1. Scrivi una 'narrativeStory' breve (max 800 caratteri) che introduca il luogo %s usando il tono: %s.
            2. Estrai una 'curiosity' intrigante dal contesto.
            3. Crea un 'quiz' a 4 opzioni basato sui fatti reali, con difficoltà: %s.
            
            IMPORTANTE PER IL QUIZ:
            - L'indice 'correctAnswerIndex' deve essere 0-based (0 = prima opzione, 1 = seconda opzione, ecc.).
            - Assicurati che la risposta indicata dall'indice corrisponda esattamente alla risposta corretta tra le 'options'.
            
            %s
            """,
                    archetype.getDisplayName(),
                    context,
                    name,
                    archetype.getNarrativeEngineConfig().getToneOfVoice(),
                    archetype.getGameplayLogic().getDifficultyMultiplier(),
                    converter.getFormat()
            );

        Prompt prompt = new Prompt(
                systemPromptTesto,
                OpenAiChatOptions.builder()
                        .model("gpt-5.4-mini") // Ricordati il prefisso per Regolo!
                        .temperature(1.0) // Alziamo un po' per il Ninja o altri archetipi creativi
                        .build()
        );
        ChatResponse response = chatModel.call(prompt);
        return converter.convert(response.getResult().getOutput().getText());
    }
}
