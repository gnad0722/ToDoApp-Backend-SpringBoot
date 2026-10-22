package dev.gnad.SpringModulith.common.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class PolicyFacade {

    private final TimePolicy timePolicy;

    public PolicyFacade(TimePolicy timePolicy) {
        this.timePolicy = timePolicy;
    }


    public void validateDueDate(LocalDateTime dueDate) {
        timePolicy.validateDueDate(dueDate);
    }

    public void validateTimeRange(
            LocalDateTime start,
            LocalDateTime end){
        timePolicy.validateExpectedTime(start,end);
    }

}
