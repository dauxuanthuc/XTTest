package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.payload.NotificationDTO;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    // simple in-memory rate limiter per IP for stream attempts
    private final Map<String, Attempt> streamAttempts = new ConcurrentHashMap<>();
    private static final int STREAM_WINDOW_SECONDS = 60;
    private static final int STREAM_MAX_ATTEMPTS = 5;

    private static class Attempt {
        long windowStart;
        int count;
        Attempt(long windowStart, int count) { this.windowStart = windowStart; this.count = count; }
    }

    public NotificationController(NotificationService notificationService, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    // Authenticated user: get own notifications
    @GetMapping
    public ResponseEntity<?> getForCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String username = authentication.getName();
        List<NotificationDTO> list = notificationService.getNotificationsForUsername(username);
        return ResponseEntity.ok(list);
    }

    // Authenticated user: mark own notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        Optional<Notification> opt = notificationRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Notification n = opt.get();
        String username = authentication.getName();
        if (n.getRecipient() == null || !username.equals(n.getRecipient().getUsername())) {
            return ResponseEntity.status(403).body("Không có quyền");
        }
        n.setIsRead(true);
        notificationRepository.save(n);
        return ResponseEntity.ok().build();
    }

    // SSE stream for notifications (for real-time updates)
    @GetMapping("/stream")
    public ResponseEntity<SseEmitter> stream(Authentication authentication, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        long now = Instant.now().getEpochSecond();
        Attempt a = streamAttempts.get(clientIp);
        if (a == null || now - a.windowStart > STREAM_WINDOW_SECONDS) {
            a = new Attempt(now, 0);
            streamAttempts.put(clientIp, a);
        }
        a.count++;
        if (a.count > STREAM_MAX_ATTEMPTS) {
            long retryAfter = STREAM_WINDOW_SECONDS - (now - a.windowStart);
            if (retryAfter < 0) retryAfter = STREAM_WINDOW_SECONDS;
            return ResponseEntity.status(429).header("Retry-After", String.valueOf(retryAfter)).build();
        }

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        SseEmitter emitter = notificationService.createEmitterForUser(username);
        // send an initial event to confirm connection
        try {
            emitter.send(SseEmitter.event().name("connected").data("connected"));
        } catch (Exception e) {
            // ignore
        }

        // reset attempt count on successful connection
        streamAttempts.remove(clientIp);
        return ResponseEntity.ok(emitter);
    }
}
