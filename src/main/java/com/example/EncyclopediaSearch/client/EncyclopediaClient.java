package com.example.EncyclopediaSearch.client;


import com.example.EncyclopediaSearch.exception.EncyclopediaApiException;
import com.example.EncyclopediaSearch.vo.EncyclopediaSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EncyclopediaClient {
    private final WebClient encyclopediaWebClient;
    private final String clientId;
    private final String clientSecret;
    private final int maxResults;

    public EncyclopediaClient(WebClient encyclopediaWebClient,
                         @Value("${naver.api.client-id}") String clientId,
                         @Value("${naver.api.client-secret}") String clientSecret,
                         @Value("${encyclopedia.api.default-display:10}") int maxResults) {
        this.encyclopediaWebClient = encyclopediaWebClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.maxResults = maxResults;
    }

    public Mono<EncyclopediaSearchResponse> searchEncyclopedias(String keyword) {
        return encyclopediaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/encyc.json")
                        .queryParam("query", keyword)
                        .queryParam("display", maxResults)
                        .build()
                )
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new EncyclopediaApiException("Naver API error: " + errorBody))))
                .bodyToMono(EncyclopediaSearchResponse.class);
    }

}
