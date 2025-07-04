package com.luis.pdfreader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiSummaryService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public Mono<String> extractData(String inputText) {
        String prompt = """
                Analiza el siguiente texto legal y responde en JSON con esta estructura:
                {
                    "fiscal": { "nombre": "" },
                    "imputado": { "nombre": "", "dni": "", "edad": "", "departamento": "", "provincia": "", "distrito": "", "profesion": "" },
                    "agraviado": { "nombre": "", "dni": "", "departamento": "", "provincia": "", "distrito": "", "profesion": "" },
                    "resumen": "..."
                }
                Texto:
                """ + inputText;

        Map<String, Object> request = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiKey).build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }
}
