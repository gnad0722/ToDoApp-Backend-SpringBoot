package dev.gnad.SpringModulith.common.policy;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BirthDayPolicy {

    public void validateBirthday(LocalDateTime birthDay){
        if (birthDay !=null && birthDay.isAfter(LocalDateTime.now())){
            throw new BadRequestException(
                    "The birth day cannot be in the future."
            );
        }
    }
}
