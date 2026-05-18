package com.auction.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String name;

    private String email;

    private String role;

    private String password;

    private Double usedDeposit;

    private Double deposit;
}