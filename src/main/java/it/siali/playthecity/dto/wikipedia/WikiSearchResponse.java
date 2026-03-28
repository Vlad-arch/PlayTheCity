package it.siali.playthecity.dto.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiSearchResponse(WikiQuery query) {}
