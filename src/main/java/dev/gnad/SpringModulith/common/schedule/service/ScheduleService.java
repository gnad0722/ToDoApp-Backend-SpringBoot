package dev.gnad.SpringModulith.common.schedule.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.email.utility.EmailGenerator;
import dev.gnad.SpringModulith.common.notification.domain.MessageType;
import dev.gnad.SpringModulith.common.notification.service.NotificationService;
import dev.gnad.SpringModulith.common.schedule.domain.Enum.ScheduleStatus;
import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;
import dev.gnad.SpringModulith.common.schedule.internal.repository.ScheduleRepository;
import dev.gnad.SpringModulith.common.schedule.policy.SchedulePolicy;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;

    private final SchedulePolicy schedulePolicy;
    private final NotificationService notificationService;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           SchedulePolicy schedulePolicy,
                           NotificationService notificationService) {
        this.scheduleRepository = scheduleRepository;
        this.schedulePolicy = schedulePolicy;
        this.notificationService = notificationService;
    }

    public void createSchedule(ToDoList toDoList){
        Schedule schedule = new Schedule();

        schedule.setToDoList(toDoList);
        schedule.setRemindAt(toDoList.getStartTime());
        scheduleRepository.save(schedule);
    }

    public void updateRemindTime(ToDoList toDoList) {
        Schedule schedule = scheduleRepository.findByToDoList_id(toDoList.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Not Found Schedule for To Do List with Id: " + toDoList.getId()));

        boolean dayChanged = !schedule.getRemindAt().toLocalDate()
                .equals(toDoList.getStartTime().toLocalDate());

        schedule.setRemindAt(toDoList.getStartTime());
        if (dayChanged) schedule.setStatus(ScheduleStatus.PENDING);
        scheduleRepository.save(schedule);
    }

    public void cancelSchedule(ToDoList toDoList){
        Schedule schedule = this.scheduleRepository.findByToDoList_id(toDoList.getId())
                .orElseThrow(
                        ()-> new NotFoundException("Not Found Schedule for To Do List with Id: "+ toDoList.getId())
                );
        this.schedulePolicy.validateCanceled(schedule);
        schedule.setStatus(ScheduleStatus.CANCELED);
        this.scheduleRepository.save(schedule);
    }

    public void sendDailyReminder(String recipientEmail, List<Schedule> schedules) {
        List<ToDoList> toDoLists = schedules.stream()
                .map(Schedule::getToDoList)
                .toList();

        // In-app reminder for each to-do list starting today (stored + pushed via FCM).
        for (ToDoList toDoList : toDoLists) {
            try {
                notificationService.notify(
                        toDoList.getUser().getId(),
                        MessageType.TODO_LIST_DUE,
                        toDoList.getTopic());
            } catch (Exception e) {
                log.error("In-app reminder failed for to-do list {}", toDoList.getId(), e);
            }
        }

        ScheduleStatus result;
        try {
            notificationService.sendEmailMessage(
                    recipientEmail,
                    EmailGenerator.generateDailyToDoEmail(toDoLists),
                    "Your Task List Today");
            result = ScheduleStatus.SENT;
        } catch (Exception e) {
            result = ScheduleStatus.FAILED;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
        for (Schedule s : schedules) {
            s.setStatus(result);
            if (result == ScheduleStatus.SENT) s.setSendAt(now);
        }
        scheduleRepository.saveAll(schedules);
    }

    public List<Schedule> findDueSchedules(LocalDateTime startOfDay,
                                           LocalDateTime startOfNextDay,
                                           ScheduleStatus status){
        return this.scheduleRepository.findDueSchedules(
                startOfDay,
                startOfNextDay,
                status
        );
    }
}
