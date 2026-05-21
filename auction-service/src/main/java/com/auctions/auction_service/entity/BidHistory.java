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
@Table(name = "bid_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lot_no")
    private Integer lotNo;

    @Column(name = "bid_by")
    private String bidBy;

    @Column(name = "bid_amount")
    private Double bidAmount;

    @CreationTimestamp
    @Column(name = "bid_time")
    private LocalDateTime bidTime;
}
