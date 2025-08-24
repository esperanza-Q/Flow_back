package org.example.flow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GooglePlacesService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.api.key}")  // ✅ 주입
    private String apiKey;

    public List<String> fetchReviews(String placeId) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/place/details/json"
                + "?place_id=" + placeId
                + "&fields=name,rating,formatted_address,reviews"
                + "&language=ko"
                + "&key=" + apiKey;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String json = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode reviewsNode = root.path("result").path("reviews");

        List<String> reviewTexts = new ArrayList<>();
        for (JsonNode review : reviewsNode) {
            reviewTexts.add(review.path("text").asText());
        }

        return reviewTexts;
    }
}
