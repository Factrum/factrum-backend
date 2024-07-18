package com.angrybug.ysjd.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SimulationService {
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public SimulationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> callExternalApi1(String requestData) {

        String apiUrl = "http://localhost:8000/simulation/result/";

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

        String apiUrl = "http://localhost:8000/simulation/patient/";

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
