package com.talentflow.domain.notification;

import java.util.*;

public interface NotificationRepository {
    Notification save(Notification n);
    List<Notification> findByUser(UUID userId);
    List<Notification> findUnreadByUser(UUID userId);
    Optional<Notification> findById(UUID id);
    void markAllRead(UUID userId);
}
