package com.hymer.hymarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${BREVO_SENDER_EMAIL}")
    private String senderEmail;

    private static final String BREVO_URL =
            "https://api.brevo.com/v3/smtp/email";
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    @Autowired
    public MailService(RestTemplate restTemplate, HttpHeaders headers) {
        this.restTemplate = restTemplate;
        this.headers = headers;
    }

    @Async
    public void sendMail(String to,
                         String subject,
                         String htmlContent) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "NearEase");
            sender.put("email", senderEmail);

            List<Map<String, String>> toList = new ArrayList<>();
            Map<String, String> receiver = new HashMap<>();
            receiver.put("email", to);
            toList.add(receiver);

            body.put("sender", sender);
            body.put("to", toList);
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            System.out.println("Mail Sent Successfully to : " + to);
            System.out.println(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to Send Mail to : " + to);
        }
    }
}