package com.auctions.auction_service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.auctions.auction_service.enums.VehicleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

//    @Column(name = "added_by")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @Column(name = "target_selling_price")
    private Double targetSellingPrice;

    @Column(name = "min_selling_price")
    private Double minSellingPrice;

//    @Column(name = "won_by")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "won_by")
    private User wonBy;

    @Column(name = "winning_bid_amount")
    private Double winningBidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VehicleStatus status;

    @Column(name = "bid_close_date")
    private LocalDateTime bidCloseDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @Column(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User clientId;
    
    @Column(name = "current_bid_amount")
    private Double currentBidAmount;
    
//    @Column(name = "current_highest_bidder")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_highest_bidder")
    private User currentHighestBidder;
}
