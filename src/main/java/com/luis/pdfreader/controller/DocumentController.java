package com.luis.pdfreader.controller;

import com.luis.pdfreader.service.AiSummaryService;
import com.luis.pdfreader.service.GeminiSummaryService;
import com.luis.pdfreader.service.PdfParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class DocumentController {

    private final PdfParserService pdfParserService;
    private final AiSummaryService aiSummaryService;
    private final GeminiSummaryService geminiSummaryService;

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".pdf");
        file.transferTo(tempFile);
        String text = pdfParserService.extractTextFromPdf(tempFile);
        tempFile.delete();
        return ResponseEntity.ok(text);
    }

    @PostMapping("/extract-structured")
    public ResponseEntity<String> extractStructured(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".pdf");
        file.transferTo(tempFile);
        String text = pdfParserService.extractTextFromPdf(tempFile);
        tempFile.delete();

        String result = aiSummaryService.extractData(text); // ← bloquea para respuesta directa
        return ResponseEntity.ok(result);
    }



    @PostMapping("/extract-structured-gemini")
    public Mono<ResponseEntity<String>> extractStructuredWithGemini(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded-", ".pdf");
        file.transferTo(tempFile);
        String text = pdfParserService.extractTextFromPdf(tempFile);
        tempFile.delete();

        return geminiSummaryService.extractData(text)
                .map(result -> ResponseEntity.ok().body(result))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar"));
    }


}
