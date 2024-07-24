package com.angrybug.ysjd.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SimulationService {
    private final WebClient.Builder webClientBuilder;

    @Value("${service.api1url}")
    private String serviceSimulUrl1;

    @Value("${service.api2url}")
    private String serviceSimulUrl2;

    @Autowired
    public SimulationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> callExternalApi1(String requestData) {

        String apiUrl = serviceSimulUrl1;

        // WebClient를 사용하여 POST 요청 구성
        return webClientBuilder.build()
                .post()
                .uri(apiUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestData))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> callExternalApi2(String requestData) {

        String apiUrl = serviceSimulUrl2;

        // WebClient를 사용하여 POST 요청 구성
        return webClientBuilder.build()
                .post()
                .uri(apiUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestData))
                .retrieve()
                .bodyToMono(String.class);
    }
}
