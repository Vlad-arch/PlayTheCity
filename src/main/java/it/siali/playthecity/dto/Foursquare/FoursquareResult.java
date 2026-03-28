package it.siali.playthecity.dto.Foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareResult(
        @JsonProperty("fsq_id") String fsqId, // Mappa il nome strano del JSON alla variabile Java
        String name,
        List<FoursquareCategory> categories,
        FoursquareGeocodes geocodes
) {}