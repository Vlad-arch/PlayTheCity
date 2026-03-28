package it.siali.playthecity.dto.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareResult(
        @JsonProperty("fsq_place_id") // Deve mappare il nome esatto del JSON
        String fsqPlaceId,

        String name,

        // Le coordinate ora sono al primo livello!
        double latitude,
        double longitude,

        List<FoursquareCategory> categories
) {}