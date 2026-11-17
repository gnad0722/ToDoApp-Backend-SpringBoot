package dev.gnad.SpringModulith.common.schedule.processor;

import dev.gnad.SpringModulith.common.schedule.service.ScheduleReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleProcessor {
    private final ScheduleReminderService reminderService;

    public ScheduleProcessor(ScheduleReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Bangkok")
    public void processDueSchedules() {
        reminderService.processDueSchedules();
    }
}
