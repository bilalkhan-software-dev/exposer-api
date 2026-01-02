package com.exposer.models.dto;

public record SendNotificationEvent(String to, String subject, String body, String eventType) {
}
