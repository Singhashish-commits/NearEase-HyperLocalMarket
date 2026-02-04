package com.hymer.hymarket.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private  final JavaMailSender mailSender;
    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to , String subject,String content,String AlertMessage){
        try{
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ashish.singh.rajpoot02@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            System.out.println("Mail Sent Successfully to : "+ to);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to Send Mail to : "+ to);
        }

    }


}
