package com.agileoracles.leave_portal_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LlmLeaveCategorizationService {

    private final String apiKey;
    private final RestTemplate restTemplate;

    public LlmLeaveCategorizationService(
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String categorize(String reasonForLeave) {

        String prompt = """
                Classify this employee leave request into exactly one
                of these categories:

                Sick Leave
                Annual Leave
                Emergency Leave
                Maternity Leave
                Unpaid Leave
                Other

                Return only the category name.
                Do not explain your answer.

                Leave reason:
                """ + reasonForLeave;

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + "gemini-3.5-flash:generateContent";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.set(
                "x-goog-api-key",
                apiKey
        );

        Map<String, Object> requestBody =
                Map.of(
                        "contents",
                        List.of(
                                Map.of(
                                        "parts",
                                        List.of(
                                                Map.of(
                                                        "text",
                                                        prompt
                                                )
                                        )
                                )
                        )
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        Map response =
                restTemplate.postForObject(
                        url,
                        request,
                        Map.class
                );

        if (response == null) {
            return "Other";
        }

        List candidates =
                (List) response.get("candidates");

        if (candidates == null ||
                candidates.isEmpty()) {

            return "Other";
        }

        Map candidate =
                (Map) candidates.get(0);

        Map content =
                (Map) candidate.get("content");

        List parts =
                (List) content.get("parts");

        if (parts == null ||
                parts.isEmpty()) {

            return "Other";
        }

        Map part =
                (Map) parts.get(0);

        String category =
                (String) part.get("text");

        if (category == null) {
            return "Other";
        }

        return category.trim();
    }
}