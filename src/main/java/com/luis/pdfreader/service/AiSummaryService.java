package com.luis.pdfreader.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiSummaryService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

   /* private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();*/

    public String/*Mono<String>*/ extractData(String extractedText) {
        OpenAiService service = new OpenAiService(openaiApiKey);

        String prompt = """
            Lee el siguiente texto legal extraído de un documento PDF y responde en JSON con los siguientes datos:
            {
              "fiscal": { "nombre": "" },
              "imputado": { "nombre": "", "dni": "", "edad": "", "departamento": "", "provincia": "", "distrito": "", "profesion": "" },
              "agraviado": { "nombre": "", "dni": "", "departamento": "", "provincia": "", "distrito": "", "profesion": "" },
              "resumen": "resumen breve del caso"
            }
            Texto:
            """ + extractedText;

        /*Map<String, Object> body = Map.of(
                "model", "gpt-4",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                },
                "temperature", 0.2
        );

        return webClient.post()
                .header("Authorization", "Bearer " + openaiApiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);*/

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo") // Puedes usar "gpt-3.5-turbo" si es más económico
                .messages(List.of(new ChatMessage("user", prompt)))
                .temperature(0.3)
                .build();

        ChatCompletionResult result = service.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}
