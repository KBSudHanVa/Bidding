package com.auctions.auction_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_transaction_type")
    private String paymentTransactionType;

    @Column(name = "payment_type")
    private String paymentType;

    private BigDecimal amount;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "vehicle_lot_no")
    private Integer vehicleLotNo;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "approved_by")
    private String approvedBy;

    private String status;

    private String note;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}