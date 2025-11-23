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
    // emitter per username
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotificationForUser(User user, String message) {
        Notification n = new Notification(user, message);
        Notification saved = notificationRepository.save(n);
        // publish to SSE subscriber if present
        String username = user != null ? user.getUsername() : null;
        if (username != null) {
            SseEmitter emitter = emitters.get(username);
            if (emitter != null) {
                try {
                    NotificationDTO dto = new NotificationDTO(saved.getId(), saved.getMessage(), saved.getCreatedAt(), saved.getIsRead());
                    emitter.send(SseEmitter.event().name("notification").data(dto));
                } catch (IOException e) {
                    // remove emitter on error
                    emitters.remove(username);
                    try { emitter.completeWithError(e); } catch (Exception ex) { }
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
        emitters.put(username, emitter);
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));
        return emitter;
    }

    public List<Notification> getAdminsAndCreateDailyNote(String message) {
        List<User> all = userRepository.findAll();
        List<User> admins = all.stream().filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()))).collect(Collectors.toList());
        List<Notification> created = admins.stream().map(a -> createNotificationForUser(a, message)).collect(Collectors.toList());
        return created;
    }
}
