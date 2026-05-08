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
            sender.put("name", "NearEase");
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
                        padding:40px 15px;
                        box-sizing:border-box;
                    }

                    .card{
                        max-width:520px;
                        margin:auto;
                        background:white;
                        border-radius:18px;
                        overflow:hidden;
                        box-shadow:
                        0 10px 30px rgba(0,0,0,0.08);
                    }

                    .header{
                        background:linear-gradient(
                                135deg,
                                #2563eb,
                                #1e40af
                        );
                        color:white;
                        padding:40px 20px;
                        text-align:center;
                    }

                    .header h1{
                        margin:0;
                        font-size:42px;
                        font-weight:700;
                    }

                    .header p{
                        margin-top:12px;
                        font-size:22px;
                        opacity:0.95;
                    }

                    .content{
                        padding:45px 35px;
                        color:#333;
                    }

                    .content p{
                        font-size:18px;
                        line-height:1.8;
                        margin:0 0 18px 0;
                    }

                    .otp-box{
                        margin:40px 0;
                        text-align:center;
                    }

                    .otp-title{
                        font-size:18px;
                        color:#555;
                        margin-bottom:18px;
                        font-weight:600;
                    }

                    .otp{
                        display:inline-block;
                        padding:18px 34px;
                        font-size:38px;
                        font-weight:700;
                        letter-spacing:10px;
                        color:#2563eb;
                        background:#eef4ff;
                        border-radius:14px;
                        border:2px solid #c7d7ff;
                        min-width:260px;
                        text-align:center;
                        box-shadow:
                        0 4px 12px rgba(37,99,235,0.12);
                    }

                    .expiry{
                        text-align:center;
                        margin-top:10px;
                        font-size:16px;
                        color:#555;
                    }

                    .warning{
                        margin-top:30px;
                        padding:18px;
                        background:#fff7ed;
                        border-left:5px solid #f59e0b;
                        border-radius:10px;
                        font-size:15px;
                        color:#92400e;
                        line-height:1.6;
                    }

                    .footer{
                        text-align:center;
                        padding:22px;
                        background:#f9fafb;
                        color:#777;
                        font-size:14px;
                        border-top:1px solid #e5e7eb;
                    }

                    .brand{
                        font-weight:700;
                        color:#2563eb;
                    }

                    @media only screen and (max-width:600px){

                        .header h1{
                            font-size:34px;
                        }

                        .header p{
                            font-size:18px;
                        }

                        .content{
                            padding:35px 22px;
                        }

                        .otp{
                            font-size:30px;
                            letter-spacing:8px;
                            padding:16px 24px;
                            min-width:220px;
                        }
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
                            Use the following One-Time Password (OTP)
                            to complete your verification process.
                        </p>

                        <div class="otp-box">

                            <div class="otp-title">
                                Your Verification Code
                            </div>

                            <div class="otp">
            """
                    + content +
                    """
                                    </div>
        
                                </div>
        
                                <div class="expiry">
                                    This OTP is valid for
                                    <strong>10 minutes</strong>.
                                </div>
        
                                <div class="warning">
                                    Do not share this OTP with anyone.
                                    NearEase will never ask for your OTP.
                                </div>
        
                            </div>
        
                            <div class="footer">
        
                                © 2026
                                <span class="brand">
                                    NearEase
                                </span>
        
                                <br><br>
        
                                All Rights Reserved.
        
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