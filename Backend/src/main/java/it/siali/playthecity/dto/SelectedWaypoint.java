package it.siali.playthecity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SelectedWaypoint(
        String locationName,
        String selectionReason, // Il motivo per cui l'AI lo ha scelto per quell'archetipo

        // Campi opzionali che verranno popolati dal metodo "arricchisciItinerarioConDatiOriginali"
        String imageUrl,
        double lat,
        double lon,
        String source,
        String originalDescription
) {
    // Costruttore compatto per comodità (se serve creare una versione "arricchita")
    public SelectedWaypoint(String locationName, String selectionReason, String imageUrl,
                            double lat, double lon, String source, String originalDescription) {
        this.locationName = locationName;
        this.selectionReason = selectionReason;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lon = lon;
        this.source = source;
        this.originalDescription = originalDescription;
    }
}