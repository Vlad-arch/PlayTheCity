package it.siali.playthecity.dto.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareCategory(String name) {}
