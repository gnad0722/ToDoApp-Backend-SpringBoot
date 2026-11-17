package dev.gnad.SpringModulith.common.schedule.policy;

import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;
import org.springframework.stereotype.Component;

@Component
public class SchedulePolicy {
    private final CancelSchedulePolicy cancelSchedulePolicy;

    public SchedulePolicy(CancelSchedulePolicy cancelSchedulePolicy) {
        this.cancelSchedulePolicy = cancelSchedulePolicy;
    }

    public void validateCanceled(Schedule schedule){
        this.cancelSchedulePolicy.canCancel(schedule);
    }
}
