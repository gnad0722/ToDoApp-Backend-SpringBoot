package dev.gnad.SpringModulith.module.ToDoList.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
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

    @OneToOne(
            mappedBy = "toDoList",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private Integer numTask = 0;

    @Column(nullable = false)
    private Integer numCompleteTask = 0;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getNumTask() {
        return numTask;
    }

    public void setNumTask(Integer numTask) {
        this.numTask = numTask;
    }

    public Integer getNumCompleteTask() {
        return numCompleteTask;
    }

    public void setNumCompleteTask(Integer numCompleteTask) {
        this.numCompleteTask = numCompleteTask;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
