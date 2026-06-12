package com.hymer.hymarket.service;

import org.springframework.stereotype.Service;

@Service
public class MailTemplateService {

    public String buildOtpHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>OTP Verification</title>
                    <style>
                        body { margin:0; padding:0; background:#f4f7fb; font-family:Arial,Helvetica,sans-serif; }
                        .container { width:100%; padding:40px 15px; box-sizing:border-box; }
                        .card { max-width:520px; margin:auto; background:white; border-radius:18px; overflow:hidden; box-shadow:0 10px 30px rgba(0,0,0,0.08); }
                        .header { background:linear-gradient(135deg,#2563eb,#1e40af); color:white; padding:40px 20px; text-align:center; }
                        .header h1 { margin:0; font-size:42px; font-weight:700; }
                        .header p { margin-top:12px; font-size:22px; opacity:0.95; }
                        .content { padding:45px 35px; color:#333; }
                        .content p { font-size:18px; line-height:1.8; margin:0 0 18px 0; }
                        .otp-box { margin:40px 0; text-align:center; }
                        .otp-title { font-size:18px; color:#555; margin-bottom:18px; font-weight:600; }
                        .otp { display:inline-block; padding:18px 34px; font-size:38px; font-weight:700; letter-spacing:10px; color:#2563eb; background:#eef4ff; border-radius:14px; border:2px solid #c7d7ff; min-width:260px; text-align:center; box-shadow:0 4px 12px rgba(37,99,235,0.12); }
                        .expiry { text-align:center; margin-top:10px; font-size:16px; color:#555; }
                        .warning { margin-top:30px; padding:18px; background:#fff7ed; border-left:5px solid #f59e0b; border-radius:10px; font-size:15px; color:#92400e; line-height:1.6; }
                        .footer { text-align:center; padding:22px; background:#f9fafb; color:#777; font-size:14px; border-top:1px solid #e5e7eb; }
                        .brand { font-weight:700; color:#2563eb; }
                        @media only screen and (max-width:600px) {
                            .header h1 { font-size:34px; }
                            .header p { font-size:18px; }
                            .content { padding:35px 22px; }
                            .otp { font-size:30px; letter-spacing:8px; padding:16px 24px; min-width:220px; }
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
                            <p>Use the following One-Time Password (OTP) to complete your verification process.</p>
                            <div class="otp-box">
                                <div class="otp-title">Your Verification Code</div>
                                <div class="otp">
                """ + otp + """
                                </div>
                            </div>
                            <div class="expiry">This OTP is valid for <strong>10 minutes</strong>.</div>
                            <div class="warning">Do not share this OTP with anyone. NearEase will never ask for your OTP.</div>
                        </div>
                        <div class="footer">
                            © 2026 <span class="brand">NearEase</span><br><br>All Rights Reserved.
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """;
    }

    public String buildNotificationHtml(String alertMessage, String content) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>NearEase</title>
                </head>
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;">
                    <div style="width:100%;padding:40px 15px;box-sizing:border-box;">
                        <div style="max-width:520px;margin:auto;background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.08);">

                            <div style="background:linear-gradient(135deg,#2563eb,#1e40af);padding:36px 20px;text-align:center;">
                                <h1 style="margin:0;color:white;font-size:36px;font-weight:700;letter-spacing:1px;">NearEase</h1>
                                <p style="margin:10px 0 0 0;color:rgba(255,255,255,0.92);font-size:18px;font-weight:500;">
                """ + alertMessage + """
                                </p>
                            </div>

                            <div style="padding:40px 35px;color:#333;">
                                <p style="font-size:16px;line-height:1.8;margin:0 0 20px 0;color:#444;">Hello,</p>
                                <div style="background:#f0f7ff;border-left:5px solid #2563eb;border-radius:10px;padding:20px 24px;font-size:16px;color:#1e3a5f;line-height:1.8;">
                """ + content + """
                                </div>
                                <p style="margin:28px 0 0 0;font-size:15px;color:#666;line-height:1.7;">
                                    For any queries, feel free to contact us through the <strong>NearEase app</strong>.
                                </p>
                            </div>

                            <div style="text-align:center;padding:20px;background:#f9fafb;border-top:1px solid #e5e7eb;font-size:13px;color:#888;">
                                © 2026 <strong style="color:#2563eb;">NearEase</strong> &nbsp;·&nbsp; All Rights Reserved.
                            </div>

                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}