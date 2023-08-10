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

//    private String callOpenAIApi(String conflictDescription) {
//        return "Sample resolution for: " + conflictDescription;
//    }
    @Value("${openai.apikey}")
    private String openAiApiKey;

//    private String callOpenAIApi(String conflictDescription) {
//        HttpClient httpClient = HttpClient.newHttpClient();
//
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI("https://api.openai.com/v1/engines/davinci/completions"))
//                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"Help me resolve this conflict between my roommates : " + conflictDescription + "\",\"max_tokens\":150}"))
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer " + openAiApiKey)
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
//            return response.body();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "There was an error resolving your conflict. Please try again later.";
//        }
//    }

    private String callOpenAIApi(String conflictDescription) {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("Conflict Description: " + conflictDescription);


        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.openai.com/v1/engines/davinci/completions"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"How can I address the issue where " + conflictDescription + "\",\"max_tokens\":50, \"temperature\":0.5}"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                return choicesNode.get(0).path("text").asText().trim();
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


