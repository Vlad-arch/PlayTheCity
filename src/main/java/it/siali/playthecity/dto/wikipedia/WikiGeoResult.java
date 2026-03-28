package it.siali.playthecity.dto.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiGeoResult(
        String title,
        double dist // Distanza in metri, comoda se in futuro vuoi ordinarli!
) {}
