package com.weather.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.weather.client.GeocodingClient;
import com.weather.client.OpenMeteoClient;
import com.weather.model.WeatherResponse;
import com.weather.service.WeatherService;


import java.util.Map;

public class WeatherLambdaHandler implements RequestHandler<Map<String, Object>, WeatherResponse> {

    @Override
    public WeatherResponse handleRequest(Map<String, Object> input, Context context) {
        String city = (String) input.getOrDefault("city", "Wroclaw");

        WeatherService weatherService = new WeatherService(
                new OpenMeteoClient(new GeocodingClient())
        );

        return weatherService.getWeather(city);
    }
}
