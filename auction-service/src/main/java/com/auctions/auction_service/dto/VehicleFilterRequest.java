package com.auctions.auction_service.dto;

import java.time.LocalDateTime;

import com.auctions.auction_service.enums.VehicleStatus;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleFilterRequest {

    private Integer lotNo;

    private String title;

    private String description;

    private Double targetSellingPrice;

    private Double minSellingPrice;

    private String wonBy;

    private Double winningBidAmount;

    private VehicleStatus status;

    private LocalDateTime bidCloseDate;

    private LocalDateTime createdAt;

    private String clientId;

    private Double currentBidAmount;

    private String currentHighestBidder;
}