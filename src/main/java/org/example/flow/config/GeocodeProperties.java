package org.example.flow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geocode.kakao")
public class GeocodeProperties {
    private String baseUrl = "https://dapi.kakao.com";
    private String restApiKey;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getRestApiKey() { return restApiKey; }
    public void setRestApiKey(String restApiKey) { this.restApiKey = restApiKey; }
}
