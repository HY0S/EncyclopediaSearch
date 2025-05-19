package com.example.EncyclopediaSearch.controller;

import com.example.EncyclopediaSearch.dto.EncyclopediaResultDto;
import com.example.EncyclopediaSearch.service.EncyclopediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search") // https://openapi.naver.com/v1/search/encyc.json?query=...
@RequiredArgsConstructor
public class EncyclopediaController {
    private final EncyclopediaService encyclopediaService;
    
    @GetMapping
    public Mono<ResponseEntity<List<EncyclopediaResultDto>>> search(@RequestParam(value = "keyword") String keyword){
        if (keyword == null || keyword.isBlank()) {
            log.warn("Missing or empty keyword parameter");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing request parameter: keyword");
        }

        return encyclopediaService.searchEncyclopedia(keyword)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error in searchVideos", e))
                .onErrorResume(e -> {
                    log.error("Returning 500 due to error", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}