package dev.gnad.SpringModulith.module.Note.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Note extends BaseEntity {

    @Column(nullable = false)
    private String description;

    @OneToOne()
    @JoinColumn(name = "task_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
