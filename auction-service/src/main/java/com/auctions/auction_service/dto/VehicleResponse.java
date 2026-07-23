package com.auctions.auction_service.dto;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponse {

    private Integer lotNo;

    private String title;

    private String description;

    private Double targetSellingPrice;

    private Double minSellingPrice;

    private Double currentBidAmount;

    private String currentHighestBidderName;

    private String currentHighestBidderEmail;

    private String sellerName;

    private String sellerEmail;

    private String winnerName;

    private String winnerEmail;
    
    private Double winningBidAmount;

    private String status;

    private LocalDateTime bidCloseDate;

    private LocalDateTime createdAt;
}