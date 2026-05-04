package com.weather.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.weather.client.GeocodingClient;
import com.weather.client.OpenMeteoClient;
import com.weather.model.WeatherResponse;
import com.weather.service.WeatherService;


import java.util.Map;

public class WeatherLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, WeatherResponse> {

    @Override
    public WeatherResponse handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        Map<String, String> queryParams = request.getQueryStringParameters();

        String city = "Wroclaw";

        if (queryParams != null && queryParams.get("city") != null) {
            city = queryParams.get("city").trim();
        }

        WeatherService weatherService = new WeatherService(
                new OpenMeteoClient(new GeocodingClient())
        );

        return weatherService.getWeather(city);
    }
}
