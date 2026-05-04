package com.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenMeteoClient extends BaseApiClient implements WeatherApiClient {

    private static final String WEATHER_API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m";

    private final GeocodingClient geocodingClient;
    private final ObjectMapper objectMapper;

    public OpenMeteoClient(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public double getCurrentTemperature(String city) {

        try {
            double[] coordinates = geocodingClient.getCoordinates(city);

            double latitude = coordinates[0];
            double longitude = coordinates[1];

            String requestUrl = String.format(
                    WEATHER_API_URL,
                    latitude,
                    longitude
            );

            String responseBody = executeGetRequest(
                    requestUrl,
                    "Failed to fetch weather data for city: " + city
            );

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode currentNode = rootNode.path("current");

            if (currentNode.isMissingNode()
                    || currentNode.path("temperature_2m").isMissingNode()) {

                throw new RuntimeException(
                        "Weather API response missing temperature data for city: "
                                + city
                );
            }

            return currentNode.path("temperature_2m").asDouble();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch current temperature for city: " + city,
                    e
            );
        }
    }
}