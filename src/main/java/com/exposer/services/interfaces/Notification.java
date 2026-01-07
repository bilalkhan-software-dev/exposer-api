package com.exposer.services.interfaces;

import com.exposer.models.dto.SendNotificationEvent;

public interface Notification {

    void notify(SendNotificationEvent event);

}