package com.weather.service;

import com.weather.client.WeatherApiClient;
import com.weather.model.TemperatureCategory;
import com.weather.model.WeatherResponse;



public class WeatherService {


    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherApiClient client) {
        this.weatherApiClient = client;
    }

    public WeatherResponse getWeather(String city) {
        double temperature = weatherApiClient.getCurrentTemperature(city);
        TemperatureCategory category = classifyTemperature(temperature);

        return new WeatherResponse(city, temperature, category);
    }


    public TemperatureCategory classifyTemperature(double temperature) {
        if (temperature < 0) return TemperatureCategory.FREEZING;
        if (temperature <= 10) return TemperatureCategory.COLD;
        if (temperature <= 20) return TemperatureCategory.MILD;
        if (temperature <= 30) return TemperatureCategory.WARM;
        return TemperatureCategory.HOT;
    }
}
