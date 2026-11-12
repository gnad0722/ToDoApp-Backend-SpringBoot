package dev.gnad.SpringModulith.module.Project.domain.entity;

import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Project extends ToDoList {



    private LocalDateTime expectEnd;

    @Column(nullable = false)
    private String description;


}
