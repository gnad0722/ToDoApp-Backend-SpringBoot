package dev.gnad.SpringModulith.common.schedule.policy;

import dev.gnad.SpringModulith.common.schedule.domain.Enum.ScheduleStatus;
import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;
import org.springframework.stereotype.Component;

@Component
public class CancelSchedulePolicy {
    public void canCancel(Schedule schedule){
        if (schedule.getStatus() != ScheduleStatus.PENDING){
            throw new IllegalArgumentException(
                    "This Schedule Cannot Be Canceled"
            );
        }
    }
}
