package org.example.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String apiKey;
    private final String city = "Seoul,KR"; // 원하는 도시

    public WeatherService(@Value("${weather.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTodayWeather() {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=kr",
                city, apiKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("weather")) return "Unknown";

        List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
        if (weatherList.isEmpty()) return "Unknown";

        return (String) weatherList.get(0).get("main"); // Rain, Clear, Clouds 등
    }
}
