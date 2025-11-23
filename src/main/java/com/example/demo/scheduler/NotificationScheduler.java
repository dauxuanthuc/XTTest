package com.example.demo.scheduler;

import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Run at 08:00 every day (server local time)
    @Scheduled(cron = "0 0 8 * * ?")
    public void dailyPracticeReminder() {
        String message = "Nhắc ôn bài: hãy dành vài phút để ôn tập hôm nay.\n- Truy cập Ôn bài để luyện tập ngay.";
        try {
            var created = notificationService.getAdminsAndCreateDailyNote(message);
            logger.info("Created {} daily practice reminder notifications for admins", created.size());
        } catch (Exception ex) {
            logger.error("Failed to create daily practice reminders", ex);
        }
    }
}
