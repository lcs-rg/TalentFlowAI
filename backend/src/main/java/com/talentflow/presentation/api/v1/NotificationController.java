package com.talentflow.presentation.api.v1;

import com.talentflow.domain.notification.NotificationRepository;
import com.talentflow.domain.notification.Notification;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepo;

    public NotificationController(NotificationRepository nr) { this.notificationRepo = nr; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly) {
        UUID userId = TenantContext.requireUser();
        List<Notification> list = unreadOnly
                ? notificationRepo.findUnreadByUser(userId)
                : notificationRepo.findByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(list.stream().map(this::toMap).toList()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable UUID id) {
        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        n.markRead();
        notificationRepo.save(n);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        UUID userId = TenantContext.requireUser();
        notificationRepo.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Map<String, Object> toMap(Notification n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", n.getId());
        m.put("title", n.getTitle());
        m.put("message", n.getMessage());
        m.put("type", n.getType());
        m.put("read", n.isRead());
        m.put("createdAt", n.getCreatedAt().toString());
        return m;
    }
}
