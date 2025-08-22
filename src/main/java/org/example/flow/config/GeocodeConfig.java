package org.example.flow.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(GeocodeProperties.class)
public class GeocodeConfig {

    @Bean(name = "kakaoRestTemplate")
    public RestTemplate kakaoRestTemplate(GeocodeProperties props) {
        if (props.getRestApiKey() == null || props.getRestApiKey().isBlank()) {
            throw new IllegalStateException("geocode.kakao.rest-api-key 가 비어있습니다.");
        }

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        RestTemplate rt = new RestTemplate(factory);
        rt.getInterceptors().add((req, body, ex) -> {
            req.getHeaders().add(HttpHeaders.AUTHORIZATION, "KakaoAK " + props.getRestApiKey().trim());
            return ex.execute(req, body);
        });
        return rt;
    }
}
