package dev.gnad.SpringModulith.module.Note.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class Note extends BaseEntity {

    @Column(nullable = false)
    private String description;

    @OneToOne()
    @JoinColumn(name = "task_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;


}
