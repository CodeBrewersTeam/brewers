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


//    private String callOpenAIApi(String conflictDescription) {
//        HttpClient httpClient = HttpClient.newHttpClient();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        System.out.println("Conflict Description: " + conflictDescription);
//
//
//        try {
//            String prompt = "I have a conflict with my roommate because " + conflictDescription + ". How can I address and resolve this issue?";
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI("https://api.openai.com/v1/engines/davinci/completions"))
//                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"" + prompt + "\",\"max_tokens\":100, \"temperature\":0.2}"))
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer " + openAiApiKey)
//                    .build();
//
//
//            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
//
//            JsonNode rootNode = objectMapper.readTree(response.body());
//            JsonNode choicesNode = rootNode.path("choices");
//            if (choicesNode.isArray() && choicesNode.size() > 0) {
//                return choicesNode.get(0).path("text").asText().trim();
//            } else {
//                return "Unable to resolve the conflict based on the provided description.";
//            }
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
            // Refined the Prompt for Directness:
            String prompt = "Provide concise steps to resolve the situation when " + conflictDescription;

            // Prepare the request body
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "prompt", prompt,
                    "max_tokens", 100,
                    "temperature", 0.5
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

                // Filtering out repetitions or narratives:
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


