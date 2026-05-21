package com.auctions.auction_service.entity;

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
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_no")
    private Integer lotNo;

    private String title;

    private String description;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "target_selling_price")
    private Double targetSellingPrice;

    @Column(name = "min_selling_price")
    private Double minSellingPrice;

    @Column(name = "won_by")
    private String wonBy;

    @Column(name = "winning_bid_amount")
    private Double winningBidAmount;

    private String status;

    @Column(name = "bid_close_date")
    private LocalDateTime bidCloseDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
