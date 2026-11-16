package dev.gnad.SpringModulith.module.Auth.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.Auth.domain.Enum.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {


    @Column(length = 100, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<ToDoList> toDoList;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    //Default value of this column is USER
    private Role role = Role.USER;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ToDoList> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDoList> toDoList) {
        this.toDoList = toDoList;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
