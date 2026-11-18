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
public class Project extends ToDoList {



    private LocalDateTime expectEnd;

    @Column(nullable = false)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpectEnd() {
        return expectEnd;
    }

    public void setExpectEnd(LocalDateTime expectEnd) {
        this.expectEnd = expectEnd;
    }
}
