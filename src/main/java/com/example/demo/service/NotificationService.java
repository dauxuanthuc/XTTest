package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.payload.NotificationDTO;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    // emitters per username (support multiple tabs/clients)
    private final Map<String, java.util.concurrent.CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotificationForUser(User user, String message) {
        Notification n = new Notification(user, message);
        Notification saved = notificationRepository.save(n);
        // publish to all SSE subscribers for this user (if any)
        String username = user != null ? user.getUsername() : null;
        if (username != null) {
            java.util.concurrent.CopyOnWriteArrayList<SseEmitter> list = emitters.get(username);
            if (list != null) {
                NotificationDTO dto = new NotificationDTO(saved.getId(), saved.getMessage(), saved.getCreatedAt(), saved.getIsRead());
                for (SseEmitter emitter : list) {
                    try {
                        emitter.send(SseEmitter.event().name("notification").data(dto));
                    } catch (IOException e) {
                        // remove broken emitter
                        list.remove(emitter);
                        try { emitter.completeWithError(e); } catch (Exception ex) { }
                    }
                }
                if (list.isEmpty()) {
                    emitters.remove(username);
                }
            }
        }
        return saved;
    }

    public List<NotificationDTO> getNotificationsForUsername(String username) {
        List<Notification> list = notificationRepository.findByRecipientUsernameOrderByCreatedAtDesc(username);
        return list.stream().map(n -> new NotificationDTO(n.getId(), n.getMessage(), n.getCreatedAt(), n.getIsRead())).collect(Collectors.toList());
    }

    public SseEmitter createEmitterForUser(String username) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        java.util.concurrent.CopyOnWriteArrayList<SseEmitter> list = emitters.computeIfAbsent(username, k -> new java.util.concurrent.CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(username);
        });
        emitter.onTimeout(() -> {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(username);
            try { emitter.complete(); } catch (Exception ex) {}
        });
        emitter.onError((ex) -> {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(username);
            try { emitter.completeWithError(ex); } catch (Exception e) {}
        });

        return emitter;
    }

    // Heartbeat to keep connections alive and detect stale clients
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 15000)
    public void sendHeartbeats() {
        if (emitters.isEmpty()) return;
        for (String username : emitters.keySet()) {
            java.util.concurrent.CopyOnWriteArrayList<SseEmitter> list = emitters.get(username);
            if (list == null) continue;
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } catch (IOException e) {
                    list.remove(emitter);
                    try { emitter.completeWithError(e); } catch (Exception ex) {}
                }
            }
            if (list.isEmpty()) emitters.remove(username);
        }
    }

    public List<Notification> getAdminsAndCreateDailyNote(String message) {
        List<User> all = userRepository.findAll();
        List<User> admins = all.stream().filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()))).collect(Collectors.toList());
        List<Notification> created = admins.stream().map(a -> createNotificationForUser(a, message)).collect(Collectors.toList());
        return created;
    }
}
