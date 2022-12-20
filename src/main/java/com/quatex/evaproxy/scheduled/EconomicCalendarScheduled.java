package com.quatex.evaproxy.scheduled;

import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.service.EconomicCalendarService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class EconomicCalendarScheduled {

    private final EconomicCalendarService economicCalendarService;
    private final SchedulerTaskManager schedulerTaskManager;
    private final String headerMessage;
    private final List<String> prefixesMsg;
    public final static String HIGH_IMPACT = "High";

    public EconomicCalendarScheduled(EconomicCalendarService economicCalendarService,
                                     SchedulerTaskManager schedulerTaskManager,
                                     @Value("${notification.headerMessage}") String headerMessage,
                                     @Value("#{'${notification.prefixes}'.split(',')}") List<String> prefixesMsg) {
        this.economicCalendarService = economicCalendarService;
        this.schedulerTaskManager = schedulerTaskManager;
        this.headerMessage = headerMessage;
        this.prefixesMsg = prefixesMsg;
    }

    @SchedulerLock(name = "syncEconomicEvents")
    @Scheduled(cron = "0 0 0 * * *", zone = "GMT")
    public Disposable synchronizeEconomicEvents() {
        // To assert that the lock is held (prevents misconfiguration errors)
        log.info("Run task sync economic events");
        LockAssert.assertLocked();
        LocalDate from = LocalDate.now(ZoneOffset.UTC);
        LocalDate to = from.plusDays(1);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return economicCalendarService.getEventsFromRemoteService(from, to)
                .filter(e -> StringUtils.equalsIgnoreCase(e.getImpact(), HIGH_IMPACT) && !now.isAfter(e.getDateTimeUtc()))
                .groupBy(EconomicEventDto::getDateTimeUtc, e -> new MessageData(
                        buildMessage(e.getEvent()),
                        headerMessage,
                        e.getDateTimeUtc().minusMinutes(10)
                )).flatMap(groups -> groups.reduce(MessageData::join))
                .take(4, true)
                .doOnNext(e -> schedulerTaskManager.registerTasksEconomicEvent(e.header, e.message, e.eventDate))
                .subscribe();
    }

    private String buildMessage(String message) {
        int index = ThreadLocalRandom.current().nextInt(prefixesMsg.size());
        return prefixesMsg.get(index) + " " + message;
    }

    private static class MessageData {
        String message;
        final String header;
        final LocalDateTime eventDate;

        public MessageData(String message, String header, LocalDateTime eventDate) {
            this.message = message;
            this.header = header;
            this.eventDate = eventDate;
        }

        public MessageData join(MessageData o2) {
            this.message = this.message + "\n" + o2.message;
            return this;
        }
    }
}
