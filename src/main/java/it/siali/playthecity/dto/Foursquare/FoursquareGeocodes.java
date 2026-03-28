package it.siali.playthecity.dto.Foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareGeocodes(FoursquareMain main) {}