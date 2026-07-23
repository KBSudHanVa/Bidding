package com.auctions.auction_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnonymousVehicleResponse {

    private Integer lotNo;

    private String title;

    private String description;

    private Double currentBidAmount;

    private Double targetSellingPrice;

    private LocalDateTime bidCloseDate;
}
