# Weather Temperature Service

Simple AWS Lambda service written in Java that:

* Accepts a city name
* Resolves city coordinates using Open-Meteo Geocoding API
* Fetches current temperature using Open-Meteo Weather API
* Classifies temperature into:

  * Freezing (<0°C)
  * Cold (0–9.9°C)
  * Mild (10–19.9°C)
  * Warm (20–30°C)
  * Hot (>30°C)

---

# Build

```text id="k8p2qy"
> mvn clean package
```

---

# AWS Lambda Configuration

**Runtime:** Java 21

**Handler:**

```text id="g4n7rx"
com.weather.handler.WeatherLambdaHandler::handleRequest
```

---

# Example Input

```json id="x3m8pq"
{
  "city": "Warsaw"
}
```

---

# Example Output

```json id="u5r9tw"
{
  "city": "Warsaw",
  "temperature": 18.4,
  "category": "MILD"
}
```


---

# Function URL Example:

```text
https://qxnkjqhsdoh56ca2f7fzrtiv3a0twqxn.lambda-url.eu-north-1.on.aws/?city=Berlin
```

# Notes

* Defaults to **Wroclaw** if city is missing or blank
* Uses top geocoding result (`count=1`) for ambiguous city names
* Includes basic error handling for:

  * Invalid city
  * API unavailability
  * HTTP errors
  * Timeout


# Project Structure

```text
src/main/java/com/weather
│
├── handler
│   └── WeatherLambdaHandler.java
│
├── service
│   └── WeatherService.java
│   
├── client
│   ├── BaseApiClient.java
│   ├── GeocodingClient.java
│   ├── OpenMeteoClient.java
│   └── WeatherApiClient.java
│
├── model
│   ├── WeatherRequest.java
│   ├── WeatherResponse.java
│   └── TemperatureCategory.java
```



---

# Key Design Decisions
* Dependency inversion via WeatherApiClient interface. Business logic extracted from HTTP layer, changing weather provider doesn't require changes in service
* BaseApiClient abstract class for shared HTTP logic to avoid duplication and going against DRY.
* TemperatureCategory enum for classification. Temperature ranges are expressed as an enum rather than raw strings, making comparisons type-safe and extensible. Adding a new category (e.g. SCORCHING) requires a change in one place only.


---

# Unit Testing Without the Real API

* Unit tests can be performed without HTTP calls because of WeatherApiClient interface. For example OpenMeteoClient can be replaced with a mock, implementing the same interface 
```text
   WeatherApiClient mockClient = city -> 25.0;
   WeatherService service = new WeatherService(mockClient);
 ``` 

---



# Extensibility Analysis

The current design handles adding a new weather provider pretty well. Since WeatherService depends exclusively on the WeatherApiClient interface, a new provider only requires implementing getCurrentTemperature(String city). The BaseApiClient abstract class further reduces the effort, as the new client inherits HTTP boilerplate and only needs to handle its own URL construction and JSON parsing.


The main limitation is in WeatherLambdaHandler, which hardcodes new OpenMeteoClient(new GeocodingClient()). Adding a second provider currently means manually editing the handler to decide which one to use — there is no selection mechanism.


---



# What I Would Improve

Given more time, I would add a dependency injection mechanism that selects the provider based on a configuration value, for example enviromental variable. This would mean the handler never needs to change when a new provider is added. I would also extract GeocodingClient behind its own interface, since different weather providers may handle geocoding in different way.



---
