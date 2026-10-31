
package com.example.demo.fingaurd_ai.service;

import com.example.demo.fingaurd_ai.model.AuditReport.Finding;
import com.example.demo.fingaurd_ai.model.FinancialTransaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final Logger log =
            LoggerFactory.getLogger(AiService.class);

    private final RestClient restClient;
    private final ObjectMapper mapper;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-5.5}")
    private String model;

    public AiService(ObjectMapper mapper) {
        this.restClient = RestClient.create(
                "https://api.openai.com/v1"
        );
        this.mapper = mapper;
    }

    public String explain(
            FinancialTransaction transaction,
            List<Finding> findings,
            double riskScore) {

        if (apiKey == null || apiKey.isBlank()) {
            log.error("OpenAI API key is not configured.");
            return "AI explanation unavailable. Configure the OpenAI API key.";
        }

        try {

            String transactionJson =
                    mapper.writeValueAsString(
                            transactionData(transaction)
                    );

            String findingsJson =
                    mapper.writeValueAsString(
                            findings == null ? List.of() : findings
                    );

            String prompt = """
                    Analyze the following financial transaction audit.

                    Transaction:
                    %s

                    Risk Score: %.2f out of 100

                    Findings:
                    %s

                    Provide a professional financial audit explanation.

                    Explain:
                    1. Why the transaction received this risk score.
                    2. What suspicious indicators were identified.
                    3. The possible financial risk.
                    4. What action the user should take.

                    Important:
                    - Use only the supplied transaction data and findings.
                    - Do not invent suspicious activity.
                    - Do not claim fraud is confirmed.
                    - If there are no findings, clearly state that.
                    - Keep the explanation concise and understandable.
                    """.formatted(
                    transactionJson,
                    riskScore,
                    findingsJson
            );

            var request = mapper.createObjectNode();

            request.put("model", model);

            request.put(
                    "instructions",
                    """
                    You are FinGuard AI, a professional financial
                    transaction audit assistant.

                    Analyze financial transactions carefully.
                    Explain risks using evidence.
                    Never present suspected fraud as confirmed fraud.
                    """
            );

            request.put("input", prompt);
            request.put("max_output_tokens", 400);
            request.put("store", false);

            JsonNode response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                log.error("OpenAI returned an empty response.");
                return "AI explanation could not be generated.";
            }

            // Extract generated text from the Responses API output
            for (JsonNode item : response.path("output")) {

                for (JsonNode content : item.path("content")) {

                    if ("output_text".equals(
                            content.path("type").asText())) {

                        String explanation =
                                content.path("text").asText();

                        if (!explanation.isBlank()) {
                            return explanation;
                        }
                    }
                }
            }

            log.error("OpenAI response did not contain output text: {}",
                    response);

            return "AI response did not contain an explanation.";

        } catch (Exception e) {

            log.error("OpenAI API request failed: {}", e.getMessage(), e);

            return "AI explanation temporarily unavailable. "
                    + "Rule-based risk analysis is available.";
        }
    }

    private Map<String, Object> transactionData(
            FinancialTransaction transaction) {

        // LinkedHashMap allows null values, unlike Map.of()
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("transactionId", transaction.getId());
        data.put("amount", transaction.getAmount());
        data.put("currency", transaction.getCurrency());
        data.put("category", transaction.getCategory());
        data.put("description", transaction.getDescription());
        data.put("referenceCode", transaction.getReferenceCode());
        data.put("occurredAt", transaction.getOccurredAt());

        return data;
    }
}
