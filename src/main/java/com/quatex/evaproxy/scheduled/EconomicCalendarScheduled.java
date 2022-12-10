package com.quatex.evaproxy.scheduled;

import com.quatex.evaproxy.service.EconomicCalendarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.time.LocalDate;

@AllArgsConstructor
@Component
@Slf4j
public class EconomicCalendarScheduled {

    private final EconomicCalendarService economicCalendarService;

    private final static String HIGH_IMPACT = "High";

    @Scheduled(cron = "0 0 * * * ?", zone = "GMT")
    public Disposable synchronizeEconomicEvents() {
        LocalDate from = LocalDate.of(2022, 12, 8);
        LocalDate to = LocalDate.of(2022, 12, 9);
        return economicCalendarService.deleteAll()
                .thenMany(economicCalendarService.saveAll(
                        economicCalendarService.getEventsFromRemoteService(from, to)
                                .filter(e -> StringUtils.equalsIgnoreCase(e.getImpact(), HIGH_IMPACT)))
                ).subscribe();
    }
}
