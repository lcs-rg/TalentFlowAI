package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.notification.Notification;
import com.talentflow.domain.notification.NotificationRepository;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "notifications")
class NotificationJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "company_id") UUID companyId;
    @Column(name = "user_id") UUID userId;
    @Column(name = "title") String title;
    @Column(name = "message", columnDefinition = "TEXT") String message;
    @Column(name = "type") String type;
    @Column(name = "read") boolean read;
    @Column(name = "resource_type") String resourceType;
    @Column(name = "resource_id") UUID resourceId;
    @Column(name = "created_at") Instant createdAt;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getCompanyId() { return companyId; } public void setCompanyId(UUID v) { this.companyId = v; }
    public UUID getUserId() { return userId; } public void setUserId(UUID v) { this.userId = v; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getMessage() { return message; } public void setMessage(String v) { this.message = v; }
    public String getType() { return type; } public void setType(String v) { this.type = v; }
    public boolean isRead() { return read; } public void setRead(boolean v) { this.read = v; }
    public String getResourceType() { return resourceType; } public void setResourceType(String v) { this.resourceType = v; }
    public UUID getResourceId() { return resourceId; } public void setResourceId(UUID v) { this.resourceId = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
}

interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {
    List<NotificationJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<NotificationJpaEntity> findByUserIdAndReadOrderByCreatedAtDesc(UUID userId, boolean read);
}

@Repository
class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationJpaRepository jpa;
    NotificationRepositoryImpl(NotificationJpaRepository jpa) { this.jpa = jpa; }

    @Override public Notification save(Notification n) {
        NotificationJpaEntity e = new NotificationJpaEntity();
        e.setId(n.getId()); e.setCompanyId(n.getCompanyId()); e.setUserId(n.getUserId());
        e.setTitle(n.getTitle()); e.setMessage(n.getMessage()); e.setType(n.getType());
        e.setRead(n.isRead()); e.setResourceType(n.getResourceType());
        e.setResourceId(n.getResourceId()); e.setCreatedAt(n.getCreatedAt());
        jpa.save(e); return n;
    }

    @Override public List<Notification> findByUser(UUID userId) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
    }

    @Override public List<Notification> findUnreadByUser(UUID userId) {
        return jpa.findByUserIdAndReadOrderByCreatedAtDesc(userId, false).stream().map(this::toDomain).toList();
    }

    @Override public Optional<Notification> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override public void markAllRead(UUID userId) {
        jpa.findByUserIdAndReadOrderByCreatedAtDesc(userId, false).forEach(e -> {
            e.setRead(true); jpa.save(e);
        });
    }

    private Notification toDomain(NotificationJpaEntity e) {
        Notification n = new Notification(e.getId(), e.getCompanyId(), e.getUserId(), e.getTitle(), e.getType());
        n.setMessage(e.getMessage()); n.setResourceType(e.getResourceType());
        n.setResourceId(e.getResourceId());
        if (e.isRead()) n.markRead();
        return n;
    }
}
