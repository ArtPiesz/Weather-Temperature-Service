package com.weather.model;

public class WeatherResponse {
    private String city;
    private double temperature;
    private TemperatureCategory category;

    public WeatherResponse(String city, double temperature, TemperatureCategory category) {
        this.city = city;
        this.temperature = temperature;
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public TemperatureCategory getCategory() {
        return category;
    }

}
