package com.auctions.auction_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String name;

    private String email;

    private String role;

    @Column(name = "used_deposit")
    private Double usedDeposit;

    private Double deposit;

    @Column(name = "is_active")
    private Boolean isActive;
}
