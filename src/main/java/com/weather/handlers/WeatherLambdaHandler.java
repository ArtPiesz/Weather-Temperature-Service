package com.weather.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.client.GeocodingClient;
import com.weather.client.OpenMeteoClient;
import com.weather.exception.CityNotFoundException;
import com.weather.model.WeatherResponse;
import com.weather.service.WeatherService;

import java.util.Map;

public class WeatherLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            Map<String, String> queryParams = request.getQueryStringParameters();

            String city = "Wroclaw";
            if (queryParams != null && queryParams.get("city") != null) {
                city = queryParams.get("city").trim();
            }

            WeatherService weatherService = new WeatherService(
                    new OpenMeteoClient(new GeocodingClient())
            );

            WeatherResponse weather = weatherService.getWeather(city);
            return response(200, objectMapper.writeValueAsString(weather));

        } catch (CityNotFoundException e) {
            return response(404, "{\"error\": \"City not found: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return response(500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent response(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(body);
    }
}