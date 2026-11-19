package dev.gnad.SpringModulith.common.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class PolicyFacade {

    private final TimePolicy timePolicy;
    private final BirthDayPolicy birthDayPolicy;

    public PolicyFacade(TimePolicy timePolicy,
                        BirthDayPolicy birthDayPolicy) {
        this.timePolicy = timePolicy;
        this.birthDayPolicy = birthDayPolicy;
    }


    public  void validateDueDate(LocalDateTime dueDate) {
        timePolicy.validateDueDate(dueDate);
    }

    public  void validateTimeRange(
            LocalDateTime start,
            LocalDateTime end){
        timePolicy.validateExpectedTime(start,end);
    }

    public void validateBirthday(LocalDateTime birthday){
        birthDayPolicy.validateBirthday(birthday);
    }
}
