package com.quatex.evaproxy.scheduled;

import com.quatex.evaproxy.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.ClockProvider;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class SchedulerTaskManager {

    private final NotificationService notificationService;
    private final LockingTaskExecutor executor;
    private final ThreadPoolTaskScheduler taskScheduler;

    public SchedulerTaskManager(NotificationService notificationService,
                                LockingTaskExecutor executor,
                                ThreadPoolTaskScheduler taskScheduler) {
        this.notificationService = notificationService;
        this.executor = executor;
        this.taskScheduler = taskScheduler;
    }

    public void registerTasksEconomicEvent(String header, String message, LocalDateTime eventDate) {
        taskScheduler.schedule(() ->
                        executor.executeWithLock(
                                (Runnable) () -> {
                                    // To assert that the lock is held (prevents misconfiguration errors)
                                    LockAssert.assertLocked();
                                    notificationService.push(header, message)
                                            .doOnSuccess(result -> log.info("Event \"{}\" was sent. Result {}", message, result))
                                            .subscribe();
                                },
                                new LockConfiguration(ClockProvider.now(),
                                        "pushEconomicEventNotification",
                                        Duration.ofSeconds(1),
                                        Duration.ZERO)),
                eventDate.toInstant(ZoneOffset.UTC)
        );
        log.info("Event \"{}\" is registered. Event date {}", message, eventDate);
    }
}
