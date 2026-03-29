package it.siali.playthecity.dto;

import java.util.Map;

public record FinalizeRequest(String sessionId, String city, String latitude, String longitude, String age, Map<String, String> answers) {}

