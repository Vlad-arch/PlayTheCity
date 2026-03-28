package it.siali.playthecity.dto.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareMain(double latitude, double longitude) {}
