package com.stu.dormitory.common.service;

import com.stu.dormitory.config.BrevoConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final BrevoConfig brevoConfig;

    public void sendEmail(
            String to,
            String subject,
            String htmlContent
    ) {

        String url =
                "https://api.brevo.com/v3/smtp/email";

        RestTemplate restTemplate =
                new RestTemplate();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.set(
                "api-key",
                brevoConfig.getApiKey()
        );

        Map<String, Object> body =
                Map.of(

                        "sender", Map.of(
                                "name",
                                brevoConfig.getSenderName(),

                                "email",
                                brevoConfig.getSenderEmail()
                        ),

                        "to", List.of(
                                Map.of("email", to)
                        ),

                        "subject", subject,

                        "htmlContent", htmlContent
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
    }
}