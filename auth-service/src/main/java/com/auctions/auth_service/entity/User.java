package com.auctions.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @Column(name = "used_deposit")
    private Double usedDeposit;

    @Column(name = "deposit")
    private Double deposit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void generateId() {

        if (this.userId == null) {
            this.userId = java.util.UUID.randomUUID().toString();
        }
    }
}