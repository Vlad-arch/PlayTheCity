package it.siali.playthecity.dto.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareResult(
        @JsonProperty("fsq_place_id")
        String fsqPlaceId,

        String name,

        // Coordinate al primo livello (grazie al parametro 'fields' nella query)
        double latitude,
        double longitude,

        // Descrizione del luogo
        String description,

        // Lista delle categorie
        List<FoursquareCategory> categories,

        // Lista delle foto (per prefix e suffix)
        List<FoursquarePhoto> photos
) {}