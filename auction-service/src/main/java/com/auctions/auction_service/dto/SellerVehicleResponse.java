package com.auctions.auction_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerVehicleResponse {

    private Integer lotNo;

    private String title;

    private String description;

    private Double targetSellingPrice;

    private Double minSellingPrice;

    private String status;

    private LocalDateTime bidCloseDate;
}
