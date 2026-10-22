package dev.gnad.SpringModulith.module.ToDoList.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class ToDoList extends BaseEntity {

    private String topic;

    @OneToMany(
            mappedBy = "toDoList",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Task> taskList ;


    @ManyToOne()
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private Integer numTask = 0;

    @Column(nullable = false)
    private Integer numCompleteTask = 0;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();
}
