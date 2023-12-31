package com.roomEase.brewersproj.controllers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.Map;

@Controller
public class ConflictResolution {
    @GetMapping("/resolveConflict")
    public String getConflictResolutionPage(Principal principal, Model model) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        return "resolveConflict";
    }

    @Value("${openai.apikey}")
    private String openAiApiKey;

    private String callOpenAIApi(String conflictDescription) {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("Conflict Description: " + conflictDescription);

        try {
            String prompt = "Suggest a solution to the issue of " + conflictDescription;

            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "prompt", prompt,
                    "max_tokens", 150,
                    "temperature", 0.7
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.openai.com/v1/engines/davinci/completions"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                String responseText = choicesNode.get(0).path("text").asText().trim();

                if (responseText.contains(conflictDescription)) {
                    return "Please provide more details or rephrase your conflict for a better solution.";
                } else {
                    return responseText;
                }
            } else {
                return "Unable to resolve the conflict based on the provided description.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "There was an error resolving your conflict. Please try again later.";
        }
    }



    @PostMapping("/resolveConflict")
    public String postConflictResolution(String conflictDescription, Model model, Principal principal) {
        String resolution = callOpenAIApi(conflictDescription);

        model.addAttribute("resolution", resolution);
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        return "resolveConflict";
    }
}


