package dev.gnad.SpringModulith.common.schedule.domain.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.common.schedule.domain.Enum.ScheduleStatus;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
public class Schedule extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "to_do_id"
    )
    @JsonManagedReference
    private ToDoList toDoList;

    @Column(nullable = false)
    private LocalDateTime remindAt;

    @Column(nullable = false)
    private ScheduleStatus status = ScheduleStatus.PENDING;

    @Column()
    private LocalDateTime sendAt;

}
