package org.example.flow.geo.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.http.HttpHeaders;

@Configuration
public class WebClientConfig {


    @Value("${kakao.api.base-url}")
    private String baseUrl;


    @Value("${kakao.api.rest-key}")
    private String restKey;


    @Bean
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restKey)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                        .build())
                .build();
    }
}
