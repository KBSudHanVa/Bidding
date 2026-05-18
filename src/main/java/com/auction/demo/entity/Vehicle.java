package com.auction.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_no")
    private Integer lotNo;

    private String title;

    @Column(columnDefinition = "TEXT")
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}