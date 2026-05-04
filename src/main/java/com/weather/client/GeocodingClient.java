package com.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeocodingClient extends BaseApiClient {

    private static final String GEOCODING_API_URL =
            "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1";

    private final ObjectMapper objectMapper;

    public GeocodingClient() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns coordinates:
     * [0] = latitude
     * [1] = longitude
     */
    public double[] getCoordinates(String city) {

        try {
            String encodedCity = URLEncoder.encode(
                    city,
                    StandardCharsets.UTF_8
            );

            String requestUrl = String.format(
                    GEOCODING_API_URL,
                    encodedCity
            );

            String responseBody = executeGetRequest(
                    requestUrl,
                    "Failed to fetch geocoding data for city: " + city
            );

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.path("results");

            if (!resultsNode.isArray() || resultsNode.isEmpty()) {
                throw new RuntimeException("City not found: " + city);
            }

            JsonNode firstResult = resultsNode.get(0);

            double latitude = firstResult.path("latitude").asDouble();
            double longitude = firstResult.path("longitude").asDouble();

            return new double[]{latitude, longitude};

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to resolve coordinates for city: " + city,
                    e
            );
        }
    }
}