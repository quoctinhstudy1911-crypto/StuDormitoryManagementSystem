package com.stu.dormitory.common.service;

import com.stu.dormitory.config.BrevoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final BrevoConfig brevoConfig;
    private final RestTemplate restTemplate;

    public void sendEmail(String to, String subject, String htmlContent) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoConfig.getApiKey());

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", brevoConfig.getSenderName(),
                        "email", brevoConfig.getSenderEmail()
                ),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to {}", to);
            } else {
                log.warn("Email send failed to {}: status={}, body={}", to, response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception when sending email to {}: {}", to, e.getMessage(), e);
        }
    }
}