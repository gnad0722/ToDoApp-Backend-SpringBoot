package dev.gnad.SpringModulith.module.Task.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Task extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne()
    @JoinColumn(name = "todo_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ToDoList toDoList;


    //The default value of this field is start time of to do list which it belongs to
    private LocalDateTime expectStart;


    private LocalDateTime expectEnd;

    @Column(nullable = false)
    private boolean isDone = false;

    @OneToOne(
            mappedBy = "task",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private Note note;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpectStart() {
        return expectStart;
    }

    public void setExpectStart(LocalDateTime expectStart) {
        this.expectStart = expectStart;
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    public void setToDoList(ToDoList toDoList) {
        this.toDoList = toDoList;
    }

    public LocalDateTime getExpectEnd() {
        return expectEnd;
    }

    public void setExpectEnd(LocalDateTime expectEnd) {
        this.expectEnd = expectEnd;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
