package com.poc.integrationhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

@Service
public class N8nGatewayService {

    private final RestClient restClient;
    private final String webhookSecret;

    public N8nGatewayService(@Value("${n8n.base-url}") String n8nBaseUrl,
                              @Value("${n8n.webhook-secret}") String webhookSecret) {
        this.webhookSecret = webhookSecret;

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl(n8nBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public Map<String, Object> callWebhook(String webhookPath, Map<String, Object> payload,
                                             String tenantId, String executionId) {
        return restClient.post()
                .uri("/webhook" + webhookPath)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .header("X-Webhook-Secret", webhookSecret)
                .header("X-Tenant-ID", tenantId)
                .header("X-Execution-ID", executionId)
                .body(payload)
                .retrieve()
                .body(Map.class);
    }
}