package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.payload.NotificationDTO;
import com.example.demo.payload.AdminNotificationDTO;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AdminNotificationController(NotificationService notificationService, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // Get notifications for the authenticated admin (their own inbox)
    @GetMapping
    public ResponseEntity<?> getForAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String username = authentication.getName();
        List<NotificationDTO> list = notificationService.getNotificationsForUsername(username);
        return ResponseEntity.ok(list);
    }

    // Admin: list all notifications (management)
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        List<Notification> all = notificationRepository.findAll();
        List<AdminNotificationDTO> dto = all.stream().map(n -> new AdminNotificationDTO(n.getId(), n.getRecipient() != null ? n.getRecipient().getUsername() : null, n.getMessage(), n.getCreatedAt(), n.getIsRead())).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    public static class CreateRequest {
        public String message;
        public String targetUsername; // optional single user
        public String targetRole; // optional role name e.g. ROLE_TEACHER
        public Boolean toAll; // optional
    }

    // Admin: create notifications
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest req, Authentication authentication) {
        if (req == null || req.message == null || req.message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("message is required");
        }

        String sender = authentication != null ? authentication.getName() : null;

        List<User> targets;
        if (req.toAll != null && req.toAll) {
            targets = userRepository.findAll();
        } else if (req.targetRole != null && !req.targetRole.trim().isEmpty()) {
            String roleName = req.targetRole.trim();
            targets = userRepository.findAll().stream().filter(u -> u.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()))).collect(Collectors.toList());
        } else if (req.targetUsername != null && !req.targetUsername.trim().isEmpty()) {
            Optional<User> u = userRepository.findByUsername(req.targetUsername.trim());
            if (u.isEmpty()) return ResponseEntity.badRequest().body("target user not found");
            targets = List.of(u.get());
        } else {
            return ResponseEntity.badRequest().body("targetUsername or targetRole or toAll must be specified");
        }

        // Exclude sender from recipients
        List<User> filtered = targets.stream().filter(u -> sender == null || !sender.equals(u.getUsername())).collect(Collectors.toList());

        List<Notification> created = filtered.stream().map(u -> notificationService.createNotificationForUser(u, req.message)).collect(Collectors.toList());

        List<AdminNotificationDTO> dto = created.stream().map(n -> new AdminNotificationDTO(n.getId(), n.getRecipient() != null ? n.getRecipient().getUsername() : null, n.getMessage(), n.getCreatedAt(), n.getIsRead())).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    // Admin: mark a notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        Optional<Notification> opt = notificationRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Notification n = opt.get();
        n.setIsRead(true);
        notificationRepository.save(n);
        return ResponseEntity.ok(new AdminNotificationDTO(n.getId(), n.getRecipient() != null ? n.getRecipient().getUsername() : null, n.getMessage(), n.getCreatedAt(), n.getIsRead()));
    }
}
