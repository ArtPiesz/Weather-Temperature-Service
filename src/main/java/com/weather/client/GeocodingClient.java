package com.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GeocodingClient {

    private static final String GEOCODING_API_URL =
            "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1";

    private final ObjectMapper objectMapper;

    public GeocodingClient() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns coordinates for a given city:
     * [0] = latitude
     * [1] = longitude
     */
    public double[] getCoordinates(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String requestUrl = String.format(GEOCODING_API_URL, encodedCity);

            HttpURLConnection connection =
                    (HttpURLConnection) new URL(requestUrl).openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException(
                        "Failed to fetch geocoding data. HTTP error code: " + responseCode
                );
            }

            String responseBody = readResponse(connection);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.path("results");

            if (!resultsNode.isArray() || resultsNode.isEmpty()) {
                throw new RuntimeException("City not found: " + city);
            }

            JsonNode firstResult = resultsNode.get(0);

            double latitude = firstResult.path("latitude").asDouble();
            double longitude = firstResult.path("longitude").asDouble();

            return new double[]{latitude, longitude};

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch coordinates for city: " + city, e);
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            return scanner.useDelimiter("\\A").hasNext()
                    ? scanner.next()
                    : "";
        }
    }
}
