package com.auction.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bid_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "bid_time")
    private LocalDateTime bidTime;
}
