package dev.gnad.SpringModulith.common.fcm.domain.entity;

import dev.gnad.SpringModulith.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class DeviceToken extends BaseEntity {

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String token;

    public DeviceToken() {
    }

    public  DeviceToken(Integer userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
