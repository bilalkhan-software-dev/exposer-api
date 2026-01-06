package com.exposer.services;

import com.exposer.models.dto.SendNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendEmail(SendNotificationEvent emailMessage) throws UnsupportedEncodingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(emailMessage.to());
            helper.setSubject(emailMessage.subject());
            helper.setFrom(sender, "Exposer (Do not reply)");
            helper.setText(emailMessage.body(), true);
            javaMailSender.send(mimeMessage);
            log.info("Email sent to {} eventType: {}", emailMessage.to(), emailMessage.eventType());
        } catch (MessagingException e) {
            log.error("Mail send failed: body: {} : stackTrace{}", emailMessage.body(), e.getMessage());
            throw new MailSendException("Failed to send mail " + e.getMessage() + ". Please try again later.");
        }
    }
}
