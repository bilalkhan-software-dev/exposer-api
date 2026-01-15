package com.exposer.services.implementation;

import com.exposer.models.dto.SendNotificationEvent;
import com.exposer.services.interfaces.Notification;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements Notification {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notify(SendNotificationEvent event) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(event.to());
            helper.setSubject(event.subject());
            helper.setFrom(sender, "Exposer (Do not reply)");
            helper.setText(event.body(), true);
            javaMailSender.send(mimeMessage);
            log.info("Email sent to {} eventType: {}", event.to(), event.eventType());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Mail send failed: body: {} : stackTrace{}", event.body(), e.getMessage());
            throw new MailSendException("Failed to send mail. Please try again later.");
        }
    }
}
