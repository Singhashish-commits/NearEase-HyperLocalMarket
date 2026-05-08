package com.hymer.hymarket.service;

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

    @Async
    public void sendMail(String to,
                         String subject,
                         String content,
                         String alertMessage) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "HyMarket");
            sender.put("email", senderEmail);

            // Receiver
            List<Map<String, String>> toList = new ArrayList<>();

            Map<String, String> receiver = new HashMap<>();
            receiver.put("email", to);

            toList.add(receiver);

            body.put("sender", sender);
            body.put("to", toList);

            body.put("subject", subject);
            String html = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">

                        <title>OTP Verification</title>

                        <style>

                            body{
                                margin:0;
                                padding:0;
                                background:#f4f7fb;
                                font-family:Arial, Helvetica, sans-serif;
                            }

                            .container{
                                width:100%;
                                padding:40px 0;
                            }

                            .card{
                                max-width:500px;
                                margin:auto;
                                background:white;
                                border-radius:16px;
                                overflow:hidden;
                                box-shadow:0 10px 30px rgba(0,0,0,0.1);
                            }

                            .header{
                                background:linear-gradient(
                                    135deg,
                                    #2563eb,
                                    #1e40af
                                );
                                color:white;
                                padding:30px;
                                text-align:center;
                            }

                            .header h1{
                                margin:0;
                                font-size:28px;
                            }

                            .content{
                                padding:40px 30px;
                                color:#333;
                            }

                            .content p{
                                font-size:16px;
                                line-height:1.6;
                            }

                            .otp-box{
                                margin:30px 0;
                                text-align:center;
                            }

                            .otp{
                                display:inline-block;
                                padding:18px 35px;
                                font-size:36px;
                                font-weight:bold;
                                letter-spacing:10px;
                                color:#2563eb;
                                background:#eef4ff;
                                border-radius:12px;
                                border:2px dashed #2563eb;
                            }

                            .warning{
                                margin-top:20px;
                                padding:15px;
                                background:#fff4e5;
                                border-left:5px solid #f59e0b;
                                border-radius:8px;
                                font-size:14px;
                                color:#92400e;
                            }

                            .footer{
                                text-align:center;
                                padding:20px;
                                background:#f9fafb;
                                color:#666;
                                font-size:14px;
                            }

                            .brand{
                                font-weight:bold;
                                color:#2563eb;
                            }

                        </style>

                    </head>

                    <body>

                    <div class="container">

                        <div class="card">

                            <div class="header">
                                <h1>NearEase</h1>
                                <p>OTP Verification</p>
                            </div>

                            <div class="content">

                                <p>Hello User,</p>

                                <p>
                                    Use the following
                                    One-Time Password (OTP)
                                    to complete your verification process.
                                </p>

                                <div class="otp-box">
                                    <div class="otp">
                    """
                    + content +
                    """
                                    </div>
                                </div>

                                <p>
                                    This OTP is valid for
                                    <strong>10 minutes</strong>.
                                </p>

                                <div class="warning">
                                    Do not share this OTP with anyone.
                                    NearEase will never ask for your OTP.
                                </div>

                            </div>

                            <div class="footer">
                                © 2026
                                <span class="brand">
                                    NearEase
                                </span>.
                                All rights reserved.
                            </div>

                        </div>

                    </div>

                    </body>
                    </html>
                    """;

            body.put("htmlContent", html);
            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            BREVO_URL,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            System.out.println(
                    "Mail Sent Successfully to : "
                            + to
            );

            System.out.println(response.getBody());

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(
                    "Failed to Send Mail to : "
                            + to
            );
        }
    }
}