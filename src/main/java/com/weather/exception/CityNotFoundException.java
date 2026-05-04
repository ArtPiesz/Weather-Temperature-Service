package com.weather.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super(city);
    }
}
