package com.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OpenMeteoClient implements WeatherApiClient {


    private final GeocodingClient geocodingClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenMeteoClient(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    @Override
    public double getCurrentTemperature(String city) {
        double[] coordinates = geocodingClient.getCoordinates(city);
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        String urlString = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m",
                latitude,
                longitude
        );

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonNode root = objectMapper.readTree(response);
            return root.path("current").path("temperature_2m").asDouble();
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch weather data", e);
        }
    }
}
