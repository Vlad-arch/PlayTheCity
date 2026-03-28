package it.siali.playthecity.dto.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiQuery(List<WikiSearchResult> search) {}
