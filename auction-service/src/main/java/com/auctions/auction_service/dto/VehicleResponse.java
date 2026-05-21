package com.auctions.auction_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponse {

    private Integer lotNo;

    private String title;

    private String description;

    private Double targetSellingPrice;

    private Double currentBidAmount;
    
    private String currentHighestBidder;

    private String status;

    private LocalDateTime bidCloseDate;
}