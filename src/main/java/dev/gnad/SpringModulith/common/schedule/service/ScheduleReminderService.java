package dev.gnad.SpringModulith.common.schedule.service;

import dev.gnad.SpringModulith.common.schedule.domain.Enum.ScheduleStatus;
import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleReminderService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleReminderService.class);
    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");

    private final ScheduleService scheduleService;

    public ScheduleReminderService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    public void processDueSchedules() {
        LocalDate today = LocalDate.now(ZONE);

        List<Schedule> schedules = scheduleService.findDueSchedules(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                ScheduleStatus.PENDING);

        Map<String, List<Schedule>> byUserEmail = schedules.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getToDoList().getUser().getEmail()));

        byUserEmail.forEach((email, userSchedules) -> {
            try {
                scheduleService.sendDailyReminder(email, userSchedules);
            } catch (Exception e) {
                log.error("Daily reminder failed for {}", email, e);
            }
        });
    }
}
