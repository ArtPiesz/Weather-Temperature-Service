package com.weather.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public abstract class BaseApiClient {

    protected String executeGetRequest(String requestUrl, String errorMessage) {
        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(requestUrl).openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException(
                        errorMessage + ". HTTP error code: " + responseCode
                );
            }

            return readResponse(connection);

        } catch (IOException e) {
            throw new RuntimeException(errorMessage, e);
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
