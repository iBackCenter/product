package com.example.productapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(5000));
        factory.setReadTimeout(Duration.ofMillis(10000));

        return RestClient.builder()
                .baseUrl(System.getenv().getOrDefault("DUMMYJSON_BASE_URL", "https://dummyjson.com"))
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
